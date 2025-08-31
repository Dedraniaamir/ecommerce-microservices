package com.msproj.productservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.*;
import java.util.stream.Collectors; /**
 * Category Entity demonstrating tree structure with collections
 */
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

    @NotBlank(message = "Category name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    // COLLECTIONS: Self-referencing tree structure
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Category> subcategories = new HashSet<>();  // SET to avoid duplicates

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();  // LIST for ordered products

    // COLLECTIONS: Tags using ElementCollection
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "category_tags", joinColumns = @JoinColumn(name = "category_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    // Constructors
    public Category() {
        super();
    }

    public Category(String name, String description) {
        super();
        this.name = name;
        this.description = description;
    }

    // Business methods using Collections and Java 8 features
    public void addSubcategory(Category subcategory) {
        subcategory.setParent(this);
        this.subcategories.add(subcategory);
    }

    public void removeSubcategory(Category subcategory) {
        subcategory.setParent(null);
        this.subcategories.remove(subcategory);
    }

    // JAVA 8 STREAMS: Get all products including from subcategories
    public List<Product> getAllProducts() {
        List<Product> allProducts = new ArrayList<>(this.products);

        // Using Stream API to collect products from subcategories
        List<Product> subcategoryProducts = subcategories.stream()
                .flatMap(subcat -> subcat.getAllProducts().stream())  // Recursive flattening
                .collect(Collectors.toList());

        allProducts.addAll(subcategoryProducts);
        return allProducts;
    }

    // JAVA 8 STREAMS: Get active products only
    public List<Product> getActiveProducts() {
        return products.stream()
                .filter(Product::isActive)  // Method reference
                .collect(Collectors.toList());
    }

    // JAVA 8 STREAMS: Count products by status
    public Map<ProductStatus, Long> getProductCountByStatus() {
        return products.stream()
                .collect(Collectors.groupingBy(
                        Product::getStatus,  // Method reference
                        Collectors.counting()
                ));
    }

    // JAVA 8 STREAMS: Get average price of products
    public OptionalDouble getAverageProductPrice() {
        return products.stream()
                .filter(Product::isActive)
                .mapToDouble(product -> product.getPrice().doubleValue())
                .average();
    }

    // Add and remove tags
    public void addTag(String tag) {
        this.tags.add(tag.toLowerCase().trim());
    }

    public void removeTag(String tag) {
        this.tags.remove(tag.toLowerCase().trim());
    }

    public boolean hasTag(String tag) {
        return this.tags.contains(tag.toLowerCase().trim());
    }

    // Check if this is a root category
    public boolean isRootCategory() {
        return this.parent == null;
    }

    // Get category path (e.g., "Electronics > Computers > Laptops")
    public String getCategoryPath() {
        if (parent == null) {
            return name;
        }
        return parent.getCategoryPath() + " > " + name;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Category getParent() { return parent; }
    public void setParent(Category parent) { this.parent = parent; }

    public Set<Category> getSubcategories() { return subcategories; }
    public void setSubcategories(Set<Category> subcategories) { this.subcategories = subcategories; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", parentId=" + (parent != null ? parent.getId() : null) +
                ", subcategoriesCount=" + subcategories.size() +
                ", productsCount=" + products.size() +
                ", tags=" + tags +
                '}';
    }
}
