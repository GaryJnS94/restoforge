package com.restoforge.api.stock.supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SupplierController.class)
class SupplierControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  SupplierService supplierService;

  @Test
  void getAllSuppliers_returns200() throws Exception {
    when(supplierService.getAllSuppliers()).thenReturn(List.of(new Supplier("Metro")));

    mockMvc.perform(get("/suppliers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Metro"));
  }

  @Test
  void createSupplier_returns201() throws Exception {
    Supplier supplier = new Supplier("Metro");
    when(supplierService.createSupplier(any())).thenReturn(supplier);

    mockMvc.perform(post("/suppliers")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Metro\"}"))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"));
  }

  @Test
  void createSupplier_blankName_returns400ProblemDetail() throws Exception {
    mockMvc.perform(post("/suppliers")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
  }

  @Test
  void deleteSupplier_referenced_returns409() throws Exception {
    doThrow(new SupplierReferencedException(1L)).when(supplierService).deleteSupplier(1L);

    mockMvc.perform(delete("/suppliers/1"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409));
  }

  @Test
  void updateSupplier_unknownId_returns404() throws Exception {
    when(supplierService.updateSupplier(eq(99L), any()))
        .thenThrow(new SupplierNotFoundException(99L));

    mockMvc.perform(put("/suppliers/99")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Metro\"}"))
        .andExpect(status().isNotFound());
  }
}