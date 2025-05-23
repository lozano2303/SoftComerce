package com.Cristofer.SoftComerce.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.Cristofer.SoftComerce.DTO.productDTO;
import com.Cristofer.SoftComerce.DTO.responseDTO;
import com.Cristofer.SoftComerce.model.category;
import com.Cristofer.SoftComerce.model.product;
import com.Cristofer.SoftComerce.repository.Icategory;
import com.Cristofer.SoftComerce.repository.Iproduct;

@Service
public class productService {

    @Autowired
    private Iproduct productRepository;

    @Autowired
    private Icategory categoryRepository;

    // ✅ Guardar producto
    public responseDTO save(productDTO productDTO) {
        if (!validateProduct(productDTO)) {
            return new responseDTO(HttpStatus.BAD_REQUEST.toString(), "Datos del producto inválidos");
        }

        // Obtener la categoría desde el repositorio
        Optional<category> categoryOptional = categoryRepository.findById(productDTO.getCategoryID());
        if (!categoryOptional.isPresent()) {
            return new responseDTO(HttpStatus.BAD_REQUEST.toString(), "Categoría no encontrada");
        }

        product product = convertToModel(productDTO, categoryOptional.get());
        productRepository.save(product);

        return new responseDTO(HttpStatus.OK.toString(), "Producto guardado exitosamente");
    }

    // ✅ Obtener todos los productos
    public List<product> findAll() {
        return productRepository.findAll();
    }

    public List<product> filterProducts(String name, String category, Double price, Boolean status, Integer stock) {
        return productRepository.filterProducts(name, category, price, status, stock);
    }

    // ✅ Buscar producto por ID
    public Optional<product> findById(int id) {
        return productRepository.findById(id);
    }

    // ✅ Eliminar (desactivar) producto
    public responseDTO deleteProduct(int id) {
        Optional<product> product = findById(id);
        if (!product.isPresent()) {
            return new responseDTO(HttpStatus.BAD_REQUEST.toString(), "El producto no existe");
        }
    
        product productToDelete = product.get();
        productToDelete.setStatus(false);
        productRepository.save(productToDelete);
    
        return new responseDTO(HttpStatus.OK.toString(), "Producto eliminado correctamente");
    }

    // ✅ Reactivar producto

    public responseDTO updateProductStatus(int id, boolean status) {
        Optional<product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()) {
            return new responseDTO("400 BAD_REQUEST", "El producto no existe");
        }

        product productToUpdate = optionalProduct.get();
        productToUpdate.setStatus(status);
        productRepository.save(productToUpdate);

        String message = status ? "Producto activado correctamente" : "Producto desactivado correctamente";
        return new responseDTO("200 OK", message);
    }

    // ✅ Actualizar producto
    public responseDTO update(int id, productDTO productDTO) {
        // Depuración: imprimir el objeto recibido
        System.out.println("Datos recibidos en la solicitud de actualización: " + productDTO);
    
        Optional<product> existingProduct = findById(id);
        if (!existingProduct.isPresent()) {
            return new responseDTO(HttpStatus.BAD_REQUEST.toString(), "El producto no existe");
        }
    
        if (!validateProduct(productDTO)) {
            return new responseDTO(HttpStatus.BAD_REQUEST.toString(), "Datos del producto inválidos");
        }
    
        // Obtener la categoría desde el repositorio
        Optional<category> categoryOptional = categoryRepository.findById(productDTO.getCategoryID());
        if (!categoryOptional.isPresent()) {
            return new responseDTO(HttpStatus.BAD_REQUEST.toString(), "Categoría no encontrada");
        }
    
        product productToUpdate = existingProduct.get();
        productToUpdate.setName(productDTO.getName());
        productToUpdate.setDescription(productDTO.getDescription());
        productToUpdate.setPrice(productDTO.getPrice());
        productToUpdate.setStock(productDTO.getStock());
        productToUpdate.setImageUrl(productDTO.getImageUrl()); // Asegúrate de que este campo se esté mapeando correctamente
        productToUpdate.setCategory(categoryOptional.get());
    
        productRepository.save(productToUpdate);
    
        return new responseDTO(HttpStatus.OK.toString(), "✅ Producto actualizado correctamente");
    }

    // ✅ Validaciones
    private boolean validateProduct(productDTO dto) {
        return dto.getName() != null && !dto.getName().isBlank() &&
                dto.getDescription() != null && !dto.getDescription().isBlank() &&
                dto.getPrice() >= 0 &&
                dto.getStock() >= 0 &&
                dto.getCategoryID() > 0;
    }

    // ✅ Conversión de DTO a modelo
    public product convertToModel(productDTO productDTO, category category) {
        return new product(
            0,
            productDTO.getName(),
            productDTO.getDescription(),
            productDTO.getPrice(),
            productDTO.getStock(),
            LocalDateTime.now(),
            productDTO.getImageUrl(),
            true,
            category
        );
    }

    // ✅ Conversión de modelo a DTO
    public productDTO convertToDTO(product product) {
        return new productDTO(
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock(),
            product.getImageUrl(),
            product.getCategory().getcategoryID()
        );
    }
}