package com.profac.app.domain;

import static com.profac.app.domain.AppUserTestSamples.*;
import static com.profac.app.domain.CompanyTestSamples.*;
import static com.profac.app.domain.ImageTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.profac.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AppUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppUser.class);
        AppUser appUser1 = getAppUserSample1();
        AppUser appUser2 = new AppUser();
        assertThat(appUser1).isNotEqualTo(appUser2);

        appUser2.setId(appUser1.getId());
        assertThat(appUser1).isEqualTo(appUser2);

        appUser2 = getAppUserSample2();
        assertThat(appUser1).isNotEqualTo(appUser2);
    }

    @Test
    void avatarTest() throws Exception {
        AppUser appUser = getAppUserRandomSampleGenerator();
        Image imageBack = getImageRandomSampleGenerator();

        appUser.setAvatar(imageBack);
        assertThat(appUser.getAvatar()).isEqualTo(imageBack);

        appUser.avatar(null);
        assertThat(appUser.getAvatar()).isNull();
    }

    @Test
    void companyTest() throws Exception {
        AppUser appUser = getAppUserRandomSampleGenerator();
        Company companyBack = getCompanyRandomSampleGenerator();

        appUser.setCompany(companyBack);
        assertThat(appUser.getCompany()).isEqualTo(companyBack);

        appUser.company(null);
        assertThat(appUser.getCompany()).isNull();
    }
}
