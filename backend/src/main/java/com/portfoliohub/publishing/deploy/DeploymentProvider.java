package com.portfoliohub.publishing.deploy;

import com.portfoliohub.portfolio.entity.Portfolio;
import com.portfoliohub.publishing.build.BuildArtifact;

public interface DeploymentProvider {
    DeploymentResult deploy(Portfolio portfolio, BuildArtifact artifact);
    void undeploy(Portfolio portfolio);
}
