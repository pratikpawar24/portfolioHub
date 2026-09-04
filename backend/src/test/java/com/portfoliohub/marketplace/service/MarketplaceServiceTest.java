package com.portfoliohub.marketplace.service;

import com.portfoliohub.marketplace.repository.TemplateFavoriteRepository;
import com.portfoliohub.marketplace.repository.TemplateLikeRepository;
import com.portfoliohub.marketplace.repository.TemplateMarketplaceStatsRepository;
import com.portfoliohub.auth.repository.UserRepository;
import com.portfoliohub.portfolio.repository.PortfolioRepository;
import com.portfoliohub.template.entity.TemplateStatus;
import com.portfoliohub.template.entity.TemplateVisibility;
import com.portfoliohub.template.repository.TemplateRepository;
import com.portfoliohub.template.repository.TemplateVersionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

class MarketplaceServiceTest {
    private final TemplateRepository templates = mock(TemplateRepository.class);
    private final TemplateVersionRepository versions = mock(TemplateVersionRepository.class);
    private final TemplateLikeRepository likes = mock(TemplateLikeRepository.class);
    private final TemplateFavoriteRepository favorites = mock(TemplateFavoriteRepository.class);
    private final TemplateMarketplaceStatsRepository stats = mock(TemplateMarketplaceStatsRepository.class);
    private final UserRepository users = mock(UserRepository.class);
    private final PortfolioRepository portfolios = mock(PortfolioRepository.class);
    private final MarketplaceService service = new MarketplaceService(templates, versions, likes, favorites, stats, users, portfolios);

    @Test
    void searchUsesPopularRepositoryWhenPopularSortIsRequested() {
        when(templates.searchMarketplacePopular(any(), any(), isNull(), isNull(), isNull(), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));

        var result = service.search(null, null, null, "popular", PageRequest.of(0, 20), null);

        assertThat(result.getContent()).isEmpty();
        verify(templates).searchMarketplacePopular(eq(TemplateStatus.ACTIVE), eq(TemplateVisibility.PUBLIC), isNull(), isNull(), isNull(), any());
        verify(templates, never()).searchMarketplace(any(), any(), any(), any(), any(), any());
    }
}
