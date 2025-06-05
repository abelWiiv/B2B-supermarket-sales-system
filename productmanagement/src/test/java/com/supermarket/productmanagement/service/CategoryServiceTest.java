package com.supermarket.productmanagement.service;

import com.supermarket.productmanagement.dto.request.CategoryCreateRequest;
import com.supermarket.productmanagement.dto.request.CategoryUpdateRequest;
import com.supermarket.productmanagement.dto.response.CategoryResponse;
import com.supermarket.productmanagement.exception.CustomException;
import com.supermarket.productmanagement.model.Category;
import com.supermarket.productmanagement.repository.CategoryRepository;
import com.supermarket.productmanagement.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private UUID categoryId;
    private Category category;
    private CategoryCreateRequest createRequest;
    private CategoryUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        category = Category.builder()
                .id(categoryId)
                .name("Electronics")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = new CategoryCreateRequest();
        createRequest.setName("Electronics");

        updateRequest = new CategoryUpdateRequest();
        updateRequest.setName("Updated Electronics");
    }

    @Test
    void createCategory_Success() {
        when(categoryRepository.existsByName("Electronics")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponse response = categoryService.createCategory(createRequest);

        assertNotNull(response);
        assertEquals(categoryId, response.getId());
        assertEquals("Electronics", response.getName());
        verify(categoryRepository).existsByName("Electronics");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_NameAlreadyExists_ThrowsException() {
        when(categoryRepository.existsByName("Electronics")).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class,
                () -> categoryService.createCategory(createRequest));

        assertEquals("Category with name Electronics already exists", exception.getMessage());
        verify(categoryRepository).existsByName("Electronics");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void getCategoryById_Success() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        CategoryResponse response = categoryService.getCategoryById(categoryId);

        assertNotNull(response);
        assertEquals(categoryId, response.getId());
        assertEquals("Electronics", response.getName());
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void getCategoryById_NotFound_ThrowsException() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
                () -> categoryService.getCategoryById(categoryId));

        assertEquals("Category with ID " + categoryId + " not found", exception.getMessage());
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void getAllCategories_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(List.of(category));
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);

        Page<CategoryResponse> response = categoryService.getAllCategories(pageable);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("Electronics", response.getContent().get(0).getName());
        verify(categoryRepository).findAll(pageable);
    }

    @Test
    void updateCategory_Success() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("Updated Electronics")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponse response = categoryService.updateCategory(categoryId, updateRequest);

        assertNotNull(response);
        assertEquals(categoryId, response.getId());
        assertEquals("Updated Electronics", response.getName());
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).existsByName("Updated Electronics");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_NotFound_ThrowsException() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
                () -> categoryService.updateCategory(categoryId, updateRequest));

        assertEquals("Category with ID " + categoryId + " not found", exception.getMessage());
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_NameAlreadyExists_ThrowsException() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("Updated Electronics")).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class,
                () -> categoryService.updateCategory(categoryId, updateRequest));

        assertEquals("Category with name Updated Electronics already exists", exception.getMessage());
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).existsByName("Updated Electronics");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteCategory_Success() {
        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository).existsById(categoryId);
        verify(categoryRepository).deleteById(categoryId);
    }

    @Test
    void deleteCategory_NotFound_ThrowsException() {
        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class,
                () -> categoryService.deleteCategory(categoryId));

        assertEquals("Category with ID " + categoryId + " not found", exception.getMessage());
        verify(categoryRepository).existsById(categoryId);
        verify(categoryRepository, never()).deleteById(any());
    }
}