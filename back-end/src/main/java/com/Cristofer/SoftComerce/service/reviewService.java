package com.Cristofer.SoftComerce.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Cristofer.SoftComerce.DTO.responseDTO;
import com.Cristofer.SoftComerce.DTO.reviewDTO;
import com.Cristofer.SoftComerce.model.product;
import com.Cristofer.SoftComerce.model.review;
import com.Cristofer.SoftComerce.model.user;
import com.Cristofer.SoftComerce.repository.Iproduct;
import com.Cristofer.SoftComerce.repository.Ireview;
import com.Cristofer.SoftComerce.repository.Iuser;

@Service
public class reviewService {

    @Autowired
    private Ireview reviewRepository;

        // Método para obtener todas las reseñas
        public List<review> findAll() {
            return reviewRepository.findAll();
        }
    
        // Método para buscar una reseña por ID
        public Optional<review> findById(int id) {
            return reviewRepository.findById(id);
        }

            public List<reviewDTO> filterReviews(Integer rating, String comment, Integer userID, Integer productID) {
        List<review> reviews = reviewRepository.filterReviews(rating, comment, userID, productID);
        return reviews.stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    @Autowired
    private Iuser userRepository;

    @Autowired
    private Iproduct productRepository;

    // Guardar una reseña con validaciones
    @Transactional
    public responseDTO save(reviewDTO reviewDTO) {
        // Validar que el usuario exista
        Optional<user> userEntity = userRepository.findById(reviewDTO.getUserID());
        if (!userEntity.isPresent()) {
            return new responseDTO( "error","Usuario no encontrado");
        }

        // Validar que el producto exista
        Optional<product> productEntity = productRepository.findById(reviewDTO.getProductID());
        if (!productEntity.isPresent()) {
            return new responseDTO( "error","Producto no encontrado");
        }

        // Validaciones adicionales
        if (reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
            return new responseDTO( "error","La calificación debe estar entre 1 y 5");
        }

        if (reviewDTO.getComment().length() < 1 || reviewDTO.getComment().length() > 255) {
            return new responseDTO( "error", "El comentario debe estar entre 1 y 255 caracteres");
        }

        try {
            // Convertir DTO a entidad y guardar
            review reviewEntity = convertToModel(reviewDTO);
            reviewEntity.setUser(userEntity.get());
            reviewEntity.setProduct(productEntity.get());
            reviewRepository.save(reviewEntity);

            return new responseDTO( "success","Reseña registrada correctamente");
        } catch (DataAccessException e) {
            return new responseDTO( "error","Error de base de datos al guardar la reseña");
        } catch (Exception e) {
            return new responseDTO( "error","Error inesperado al guardar la reseña");
        }
    }

    // Actualizar una reseña por ID
    @Transactional
    public responseDTO update(int id, reviewDTO reviewDTO) {
        Optional<review> existingReview = reviewRepository.findById(id);
        if (!existingReview.isPresent()) {
            return new responseDTO(HttpStatus.BAD_REQUEST.toString(), "La reseña no existe");
        }

        // Validar que el usuario exista
        Optional<user> userEntity = userRepository.findById(reviewDTO.getUserID());
        if (!userEntity.isPresent()) {
            return new responseDTO( "error","Usuario no encontrado");
        }

        // Validar que el producto exista
        Optional<product> productEntity = productRepository.findById(reviewDTO.getProductID());
        if (!productEntity.isPresent()) {
            return new responseDTO( "error","Producto no encontrado");
        }

        // Validaciones adicionales
        if (reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
            return new responseDTO( "error","La calificación debe estar entre 1 y 5");
        }

        if (reviewDTO.getComment().length() < 1 || reviewDTO.getComment().length() > 255) {
            return new responseDTO("error","El comentario debe estar entre 1 y 255 caracteres");
        }

        try {
            // Actualizar datos de la reseña
            review reviewToUpdate = existingReview.get();
            reviewToUpdate.setRating(reviewDTO.getRating());
            reviewToUpdate.setComment(reviewDTO.getComment());
            reviewToUpdate.setCreatedAt(LocalDateTime.now());
            reviewToUpdate.setUser(userEntity.get());
            reviewToUpdate.setProduct(productEntity.get());

            reviewRepository.save(reviewToUpdate);

            return new responseDTO( "success","Reseña actualizada exitosamente");
        } catch (DataAccessException e) {
            return new responseDTO( "error","Error de base de datos al actualizar la reseña");
        } catch (Exception e) {
            return new responseDTO( "error","Error inesperado al actualizar la reseña");
        }
    }

    // Eliminar una reseña por ID
    @Transactional
    public responseDTO deleteById(int id) {
        Optional<review> reviewEntity = reviewRepository.findById(id);
        if (!reviewEntity.isPresent()) {
            return new responseDTO( "error","Reseña no encontrada");
        }

        try {
            reviewRepository.deleteById(id);
            return new responseDTO( "success", "Reseña eliminada correctamente");
        } catch (DataAccessException e) {
            return new responseDTO("error","Error de base de datos al eliminar la reseña");
        } catch (Exception e) {
            return new responseDTO( "error","Error inesperado al eliminar la reseña");
        }
    }

    // Convertir entidad a DTO
    public reviewDTO convertToDTO(review reviewEntity) {
        return new reviewDTO(
            reviewEntity.getRating(),
            reviewEntity.getComment(),
            reviewEntity.getUser().getUserID(),
            reviewEntity.getProduct().getProductID()
        );
    }

    // Convertir DTO a entidad
    public review convertToModel(reviewDTO reviewDTO) {
        Optional<user> userEntity = userRepository.findById(reviewDTO.getUserID());
        Optional<product> productEntity = productRepository.findById(reviewDTO.getProductID());

        if (!userEntity.isPresent() || !productEntity.isPresent()) {
            throw new IllegalArgumentException("Usuario o Producto no encontrado");
        }

        return new review(
            0, // ID autogenerado
            reviewDTO.getRating(),
            reviewDTO.getComment(),
            LocalDateTime.now(),
            userEntity.get(),
            productEntity.get()
        );
    }
}