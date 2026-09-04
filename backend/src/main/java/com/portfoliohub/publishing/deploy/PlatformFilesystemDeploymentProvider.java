package com.portfoliohub.publishing.deploy;

import com.portfoliohub.portfolio.entity.Portfolio;
import com.portfoliohub.publishing.build.BuildArtifact;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;

@Service
public class PlatformFilesystemDeploymentProvider implements DeploymentProvider {
    private final Path publishedRoot;

    public PlatformFilesystemDeploymentProvider(
            @Value("${app.publishing.published-root:${user.dir}/data/publishing/sites}") String publishedRoot) {
        this.publishedRoot = Paths.get(publishedRoot).toAbsolutePath().normalize();
    }

    @Override
    public DeploymentResult deploy(Portfolio portfolio, BuildArtifact artifact) {
        try {
            Files.createDirectories(publishedRoot);
            String safeSlug = portfolio.getSlug().replaceAll("[^a-zA-Z0-9_-]", "-");
            Path target = publishedRoot.resolve(safeSlug).normalize();
            Path temp = publishedRoot.resolve("." + safeSlug + "-" + System.nanoTime() + ".tmp").normalize();
            if (!target.getParent().equals(publishedRoot)) throw new IllegalStateException("Invalid publication path");
            deleteRecursively(temp);
            copyDirectory(artifact.directory(), temp);
            deleteRecursively(target);
            try {
                Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return new DeploymentResult(target, "/p/" + safeSlug);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to deploy platform-hosted portfolio", e);
        }
    }

    @Override
    public void undeploy(Portfolio portfolio) {
        Path target = publishedRoot.resolve(portfolio.getSlug().replaceAll("[^a-zA-Z0-9_-]", "-")).normalize();
        if (!target.getParent().equals(publishedRoot)) throw new IllegalStateException("Invalid publication path");
        try { deleteRecursively(target); } catch (IOException e) { throw new IllegalStateException("Failed to remove published site", e); }
    }

    private void copyDirectory(Path source, Path target) throws IOException {
        try (var paths = Files.walk(source)) {
            paths.forEach(path -> {
                try {
                    Path relative = source.relativize(path);
                    Path destination = target.resolve(relative).normalize();
                    if (Files.isDirectory(path)) Files.createDirectories(destination);
                    else Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) { throw new IllegalStateException(e); }
            });
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
