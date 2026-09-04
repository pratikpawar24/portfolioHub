package com.portfoliohub.publishing.build;

import java.nio.file.Path;

public record BuildArtifact(Path directory, String contentHash) {
}
