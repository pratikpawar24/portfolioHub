package com.portfoliohub.publishing.deploy;

import java.nio.file.Path;

public record DeploymentResult(Path publishedDirectory, String publicPath) {
}
