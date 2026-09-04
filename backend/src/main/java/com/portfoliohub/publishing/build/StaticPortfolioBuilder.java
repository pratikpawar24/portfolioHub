package com.portfoliohub.publishing.build;

import com.portfoliohub.portfolio.entity.Portfolio;
import com.portfoliohub.portfolio.entity.PortfolioRevision;
import com.portfoliohub.template.entity.TemplateVersion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class StaticPortfolioBuilder {
    private final JsonMapper jsonMapper;
    private final Path workspaceRoot;

    public StaticPortfolioBuilder(JsonMapper jsonMapper,
                                   @Value("${app.publishing.workspace-root:${user.dir}/data/publishing/workspaces}") String workspaceRoot) {
        this.jsonMapper = jsonMapper;
        this.workspaceRoot = Paths.get(workspaceRoot).toAbsolutePath().normalize();
    }

    public BuildArtifact build(Portfolio portfolio, PortfolioRevision revision, TemplateVersion templateVersion, String jobId) {
        try {
            Files.createDirectories(workspaceRoot);
            Path workDir = workspaceRoot.resolve(jobId).normalize();
            deleteRecursively(workDir);
            Files.createDirectories(workDir);

            if (templateVersion != null && templateVersion.getArtifactReference() != null && !templateVersion.getArtifactReference().isBlank()) {
                materializeArtifact(templateVersion.getArtifactReference(), workDir);
            }

            Path dataDir = workDir.resolve("data");
            Files.createDirectories(dataDir);
            Files.writeString(workDir.resolve("portfolio.json"), jsonMapper.writeValueAsString(revision.getContent()), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.writeString(dataDir.resolve("portfolio.json"), jsonMapper.writeValueAsString(revision.getContent()), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            Path index = workDir.resolve("index.html");
            if (!Files.exists(index)) {
                Files.writeString(index, fallbackHtml(portfolio, revision), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }

            Path meta = workDir.resolve(".portfoliohub.json");
            Files.writeString(meta, jsonMapper.createObjectNode()
                    .put("portfolioId", portfolio.getId().toString())
                    .put("revisionId", revision.getId().toString())
                    .put("schemaVersion", revision.getSchemaVersion())
                    .put("templateVersionId", templateVersion == null ? "" : templateVersion.getId().toString())
                    .toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            return new BuildArtifact(workDir, sha256Directory(workDir));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to build static portfolio artifact", e);
        }
    }

    private void materializeArtifact(String reference, Path target) throws IOException {
        String value = reference.trim();
        Path source;
        if (value.startsWith("file:")) {
            source = Paths.get(java.net.URI.create(value)).toAbsolutePath().normalize();
        } else {
            source = Paths.get(value).toAbsolutePath().normalize();
        }
        if (!Files.exists(source)) {
            throw new IllegalStateException("Template artifact does not exist: " + source);
        }
        if (Files.isDirectory(source)) {
            copyDirectory(source, target);
            return;
        }
        if (!source.getFileName().toString().toLowerCase().endsWith(".zip")) {
            throw new IllegalStateException("Template artifact must be a directory or .zip file");
        }
        extractZipSafely(source, target);
    }

    private void extractZipSafely(Path zip, Path target) throws IOException {
        try (InputStream input = Files.newInputStream(zip); ZipInputStream zis = new ZipInputStream(input)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path destination = target.resolve(entry.getName()).normalize();
                if (!destination.startsWith(target)) {
                    throw new IllegalStateException("Unsafe path in template artifact: " + entry.getName());
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(destination);
                } else {
                    Files.createDirectories(destination.getParent());
                    try (OutputStream output = Files.newOutputStream(destination, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                        zis.transferTo(output);
                    }
                }
            }
        }
    }

    private void copyDirectory(Path source, Path target) throws IOException {
        try (var paths = Files.walk(source)) {
            paths.forEach(path -> {
                try {
                    Path relative = source.relativize(path);
                    Path destination = target.resolve(relative).normalize();
                    if (Files.isDirectory(path)) Files.createDirectories(destination);
                    else Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            });
        }
    }

    private String fallbackHtml(Portfolio portfolio, PortfolioRevision revision) {
        String title = escape(portfolio.getTitle());
        String json = escape(revision.getContent().toString());
        return "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"><title>" + title + "</title><style>body{font-family:system-ui,sans-serif;max-width:960px;margin:0 auto;padding:40px;line-height:1.6}pre{white-space:pre-wrap;background:#f5f5f5;padding:20px;border-radius:12px}</style></head><body><h1>" + title + "</h1><p>This portfolio is published with PortfolioHub's default renderer.</p><pre id=\"data\"></pre><script>document.getElementById('data').textContent='" + json + "';</script></body></html>";
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("'", "\\'").replace("</script>", "<\\/script>");
    }

    private String sha256Directory(Path directory) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (var paths = Files.walk(directory).filter(Files::isRegularFile).sorted()) {
                for (Path path : paths.toList()) {
                    digest.update(directory.relativize(path).toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    digest.update(Files.readAllBytes(path));
                }
            }
            StringBuilder sb = new StringBuilder();
            for (byte b : digest.digest()) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IOException("Could not hash artifact", e);
        }
    }

    private void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) return;
        try (var paths = Files.walk(path)) {
            paths.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (IOException e) { throw new IllegalStateException(e); }
            });
        }
    }
}
