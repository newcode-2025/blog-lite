package com.example.bloglite.service.impl;

import com.example.bloglite.dto.AdminCategoryCreateRequest;
import com.example.bloglite.dto.AdminCategoryDTO;
import com.example.bloglite.dto.AdminCategoryUpdateRequest;
import com.example.bloglite.entity.Category;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.AdminCategoryMapper;
import com.example.bloglite.service.AdminCategoryService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 后台分类业务实现
 */
@Service
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private static final int DEFAULT_SORT_ORDER = 0;

    private final AdminCategoryMapper adminCategoryMapper;

    public AdminCategoryServiceImpl(
            AdminCategoryMapper adminCategoryMapper
    ) {
        this.adminCategoryMapper = adminCategoryMapper;
    }

    /**
     * 查询全部分类
     */
    @Override
    public List<AdminCategoryDTO> listCategories() {
        return adminCategoryMapper.selectAdminCategories();
    }

    /**
     * 新建分类
     */
    @Transactional
    @Override
    public AdminCategoryDTO createCategory(
            AdminCategoryCreateRequest request
    ) {
        String name = normalizeName(request.getName());

        ensureNameAvailable(name, null);

        Category category = new Category();
        category.setName(name);
        category.setSortOrder(
                request.getSortOrder() == null
                        ? DEFAULT_SORT_ORDER
                        : request.getSortOrder()
        );

        int insertedRows;

        try {
            insertedRows = adminCategoryMapper.insert(category);
        } catch (DuplicateKeyException exception) {
            throw new BizException(
                    40900,
                    "分类名称已存在"
            );
        }

        if (insertedRows != 1 || category.getId() == null) {
            throw new BizException(
                    50000,
                    "分类创建失败，请稍后重试"
            );
        }

        return loadCategory(category.getId());
    }

    /**
     * 编辑分类
     */
    @Transactional
    @Override
    public AdminCategoryDTO updateCategory(
            Long categoryId,
            AdminCategoryUpdateRequest request
    ) {
        getCategoryOrThrow(categoryId);

        String name = normalizeName(request.getName());

        ensureNameAvailable(name, categoryId);

        Category category = new Category();
        category.setId(categoryId);
        category.setName(name);
        category.setSortOrder(request.getSortOrder());

        int updatedRows;

        try {
            updatedRows = adminCategoryMapper.updateById(category);
        } catch (DuplicateKeyException exception) {
            throw new BizException(
                    40900,
                    "分类名称已存在"
            );
        }

        if (updatedRows < 1) {
            throw new BizException(
                    50000,
                    "分类更新失败，请稍后重试"
            );
        }

        return loadCategory(categoryId);
    }

    /**
     * 删除未被文章使用的分类
     */
    @Transactional
    @Override
    public void deleteCategory(Long categoryId) {
        getCategoryOrThrow(categoryId);

        long articleCount =
                adminCategoryMapper.countArticlesByCategoryId(
                        categoryId
                );

        if (articleCount > 0) {
            throw new BizException(
                    40900,
                    "分类已被文章使用，不能删除"
            );
        }

        int deletedRows = adminCategoryMapper.deleteById(categoryId);

        if (deletedRows < 1) {
            throw new BizException(
                    50000,
                    "分类删除失败，请稍后重试"
            );
        }
    }

    /**
     * 查询分类，不存在时抛出业务异常
     */
    private Category getCategoryOrThrow(Long categoryId) {
        validateCategoryId(categoryId);

        Category category = adminCategoryMapper.selectById(categoryId);

        if (category == null) {
            throw new BizException(
                    40400,
                    "分类不存在"
            );
        }

        return category;
    }

    /**
     * 重新查询分类管理数据
     */
    private AdminCategoryDTO loadCategory(Long categoryId) {
        AdminCategoryDTO category =
                adminCategoryMapper.selectAdminCategoryById(categoryId);

        if (category == null) {
            throw new BizException(
                    50000,
                    "分类数据读取失败，请稍后重试"
            );
        }

        return category;
    }

    /**
     * 校验分类 ID
     */
    private void validateCategoryId(Long categoryId) {
        if (categoryId == null || categoryId < 1) {
            throw new BizException(
                    40000,
                    "分类 ID 不正确"
            );
        }
    }

    /**
     * 校验分类名称
     */
    private String normalizeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BizException(
                    40000,
                    "分类名称不能为空"
            );
        }

        return name.trim();
    }

    /**
     * 校验分类名称是否重复
     */
    private void ensureNameAvailable(
            String name,
            Long excludeId
    ) {
        long count = adminCategoryMapper.countByCategoryName(
                name,
                excludeId
        );

        if (count > 0) {
            throw new BizException(
                    40900,
                    "分类名称已存在"
            );
        }
    }
}