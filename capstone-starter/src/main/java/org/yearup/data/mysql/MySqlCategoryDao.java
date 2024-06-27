package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{

    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        List<Category> categories = new ArrayList<>();

        try (Connection connection = dataSource.getConnection())
        {
            String sql = """
                    SELECT category_id
                        , name
                        , description
                    FROM categories;
                    """;
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet row = statement.executeQuery(sql);
            while (row.next()) {
                Category category = mapRowToCategory(row);
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

        @Override
    public Category getById(int categoryId)
    {
        try(Connection connection = dataSource.getConnection())
        {
            String sql = """
                    SELECT category_id
                        , name
                        , description
                    FROM categories
                    WHERE category_id = ?;
                    """;

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, categoryId);

            ResultSet row = statement.executeQuery();

            if(row.next())
            {
                return mapRowToCategory(row);
            }
        }
        catch (SQLException e)
        {
        }

        return null;
    }

    @Override
    public Category create(Category category)
    {
        int newId = 0;

        try(Connection connection = dataSource.getConnection())
        {
            String sql = """
                    INSERT INTO categories (name, description)
                    VALUES (?, ?);
                    """;
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            newId = generatedKeys.getInt(1);
        }
        catch (SQLException e)
        {
        }

        return getById(newId);
    }

    @Override
    public void update(int categoryId, Category category)
    {
        try(Connection connection = dataSource.getConnection())
        {
            String sql = """
                    UPDATE categories 
                    SET name = ?
                        , description = ?
                    WHERE category_id = ?;
                    """;
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());
            statement.setInt(3, categoryId);

            statement.executeUpdate();
        }
        catch (SQLException e)
        {
        }
    }

    @Override
    public void delete(int categoryId)
    {
        try (Connection connection = dataSource.getConnection()) {
            String sqlProducts = """
                    UPDATE products 
                    SET category_id = NULL
                    WHERE category_id = ?;
                    """;
            PreparedStatement statementProducts = connection.prepareStatement(sqlProducts);
            statementProducts.setInt(1, categoryId);
            statementProducts.executeUpdate();


            String sql = """
                    DELETE FROM categories
                    WHERE category_id = ?;
                    """;
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, categoryId);

            statement.executeUpdate();
        } catch (SQLException e) {
        }
    }

    @Override
    public List<Product> getProductsByCategoryId(int categoryId)
    {

        List<Product> products = new ArrayList<>();

        try (Connection connection = dataSource.getConnection())
        {
            String sql = """
                    SELECT product_id
                        , name
                        , description
                    FROM products
                    WHERE category_id = ?
                    """;
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet row = statement.executeQuery(sql);
            while (row.next()) {
                Product product = mapRowToProduct(row);
                products.add((Product) products);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    private Category mapRowToCategory(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String categoryName = row.getString("name");
        String description = row.getString("description");

        return new Category(categoryId, categoryName, description);
    }
    private Product mapRowToProduct(ResultSet row) throws SQLException
    {
        int productId = row.getInt("product_id");
        String productName = row.getString("name");
        String description = row.getString("description");

        return new Product(productId, productName, description);
    }
}
