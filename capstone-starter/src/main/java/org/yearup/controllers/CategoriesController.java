package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/categories")
@CrossOrigin
public class CategoriesController
{
    private CategoryDao categoryDao;
    private ProductDao productDao;


    @Autowired
    public CategoriesController(CategoryDao categoriesDao)
    {
        this.categoryDao = categoriesDao;
    }

    @GetMapping
    public List<Category> getAll()
    {
        return categoryDao.getAllCategories();
    }

    @GetMapping("{id}")
    public Category getById(@PathVariable int id)
    {
        return categoryDao.getById(id);
    }

    // the url to return all products in category 1 would look like this
    // https://localhost:8080/categories/1/products
    //changed this from @GetMapping to @RequestMapping
    @RequestMapping(path = "/categories/{categoryId}/products", method = RequestMethod.GET)
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        return categoryDao.getProductsByCategoryId(categoryId);
    }


    @PostMapping("/categories")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Category addNewCategory(@RequestBody Category category)
    {
        return categoryDao.create(category);
        // add didn't appear for me. Is it the same as create?
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        categoryDao.update(id, category);
    }


    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable int id) throws SQLException
    {
        categoryDao.delete(id);
//        try
//        {
//            categoryDao.delete(id);
//        }
//        catch (SQLException e)
//        {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot delete a category that has products.");
//        }
    }
}

