package com.profac.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.profac.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InvoiceProductDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InvoiceProductDTO.class);
        InvoiceProductDTO invoiceProductDTO1 = new InvoiceProductDTO();
        invoiceProductDTO1.setId(1L);
        InvoiceProductDTO invoiceProductDTO2 = new InvoiceProductDTO();
        assertThat(invoiceProductDTO1).isNotEqualTo(invoiceProductDTO2);
        invoiceProductDTO2.setId(invoiceProductDTO1.getId());
        assertThat(invoiceProductDTO1).isEqualTo(invoiceProductDTO2);
        invoiceProductDTO2.setId(2L);
        assertThat(invoiceProductDTO1).isNotEqualTo(invoiceProductDTO2);
        invoiceProductDTO1.setId(null);
        assertThat(invoiceProductDTO1).isNotEqualTo(invoiceProductDTO2);
    }
}
