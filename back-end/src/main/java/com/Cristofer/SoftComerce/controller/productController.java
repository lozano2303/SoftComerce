package com.Cristofer.SoftComerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Cristofer.SoftComerce.DTO.productDTO;
import com.Cristofer.SoftComerce.DTO.responseDTO;
import com.Cristofer.SoftComerce.model.product;
import com.Cristofer.SoftComerce.service.productService;

@RestController
@RequestMapping("/api/v1/products")
public class productController {

    @Autowired
    private productService productService;

    // ✅ Crear producto
    @PostMapping("/")
    public ResponseEntity<Object> registerProduct(@RequestBody productDTO productDTO) {
        responseDTO response = productService.save(productDTO);
        if (response.getStatus().equals(HttpStatus.OK.toString())) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // ✅ Obtener todos los productos
    @GetMapping("/")
    public ResponseEntity<Object> getAllProducts() {
        List<product> products = productService.findAll();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // ✅ Obtener un producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable int id) {
        var product = productService.findById(id);
        if (!product.isPresent()) {
            return new ResponseEntity<>("Producto no encontrado", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(product.get(), HttpStatus.OK);
    }

    // ✅ Actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable int id, @RequestBody productDTO productDTO) {
        responseDTO response = productService.update(id, productDTO);
        if (response.getStatus().equals(HttpStatus.OK.toString())) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // ✅ Eliminar (desactivar) producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable int id) {
        responseDTO response = productService.deleteProduct(id);
        if (response.getStatus().equals(HttpStatus.OK.toString())) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // ✅ Actualizar estado del producto (activar/desactivar)
    @PatchMapping("/{id}/status")
    public ResponseEntity<Object> updateProductStatus(@PathVariable int id, @RequestParam boolean status) {
        responseDTO response = productService.updateProductStatus(id, status);
        if (response.getStatus().equals("200 OK")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<Object> reactivateProduct(@PathVariable int id) {
        return updateProductStatus(id, true); // Reutilizar updateProductStatus para reactivar
    }
    // ✅ Filtro de productos (por nombre, descripción, precio, estado, categoría)
    @GetMapping("/filter")
    public ResponseEntity<Object> filterProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false) Integer categoryID
    ) {
        List<product> productList = productService.filterProducts(name, description, price, status, categoryID);
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }
}