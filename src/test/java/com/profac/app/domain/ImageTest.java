package com.profac.app.domain;

import static com.profac.app.domain.AppUserTestSamples.*;
import static com.profac.app.domain.ImageTestSamples.*;
import static com.profac.app.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.profac.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ImageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Image.class);
        Image image1 = getImageSample1();
        Image image2 = new Image();
        assertThat(image1).isNotEqualTo(image2);

        image2.setId(image1.getId());
        assertThat(image1).isEqualTo(image2);

        image2 = getImageSample2();
        assertThat(image1).isNotEqualTo(image2);
    }

    @Test
    void appUserTest() throws Exception {
        Image image = getImageRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        image.setAppUser(appUserBack);
        assertThat(image.getAppUser()).isEqualTo(appUserBack);
        assertThat(appUserBack.getAvatar()).isEqualTo(image);

        image.appUser(null);
        assertThat(image.getAppUser()).isNull();
        assertThat(appUserBack.getAvatar()).isNull();
    }

    @Test
    void productTest() throws Exception {
        Image image = getImageRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        image.setProduct(productBack);
        assertThat(image.getProduct()).isEqualTo(productBack);

        image.product(null);
        assertThat(image.getProduct()).isNull();
    }
}
