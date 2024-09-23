/*
 * Copyright (C) 2024-2024 Sermant Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sermant.backend.service;

import io.sermant.backend.common.conf.DynamicConfig;
import io.sermant.backend.entity.config.Result;
import io.sermant.backend.entity.config.ResultCodeType;
import io.sermant.backend.entity.template.PageTemplateInfo;
import io.sermant.backend.yaml.FieldMappingConstructor;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Template service for configuration management page
 *
 * @author zhp
 * @since 2024-08-22
 */
@Service
public class PageTemplateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageTemplateService.class);

    private final List<PageTemplateInfo> pageTemplateInfoList = new ArrayList<>();

    private final Yaml yaml = new Yaml(new FieldMappingConstructor(new LoaderOptions()));

    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    @Resource
    private DynamicConfig dynamicConfig;

    /**
     * Get the template information for configuration management page
     *
     * @return the template information
     */
    public Result<List<PageTemplateInfo>> getTemplateList() {
        return new Result<>(ResultCodeType.SUCCESS, pageTemplateInfoList);
    }

    /**
     * Get the template information for the specified plugin on the configuration management page
     *
     * @param pluginName plugin name
     * @return the template information
     */
    public Result<PageTemplateInfo> getTemplate(String pluginName) {
        if (CollectionUtils.isEmpty(pageTemplateInfoList)) {
            return new Result<>(ResultCodeType.FAIL);
        }
        for (PageTemplateInfo pageTemplateInfo : pageTemplateInfoList) {
            if (pageTemplateInfo.getPlugin() != null
                    && StringUtils.equals(pluginName, pageTemplateInfo.getPlugin().getEnglishName())) {
                return new Result<>(ResultCodeType.SUCCESS, pageTemplateInfo);
            }
        }
        return new Result<>(ResultCodeType.FAIL);
    }

    @PostConstruct
    private void init() {
        try {
            org.springframework.core.io.Resource resource = resolver.getResource("classpath:template");
            if (resource.exists()) {
                String realPath = Paths.get(resource.getURI()).toFile().getAbsolutePath();
                loadTemplateFile(realPath);
            }
            if (StringUtils.isEmpty(dynamicConfig.getTemplatePath())) {
                return;
            }
            loadTemplateFile(dynamicConfig.getTemplatePath());
        } catch (IOException | FileSystemNotFoundException e) {
            LOGGER.error("Failed to obtain page rendering template for Backend configuration management function", e);
        }
    }

    /**
     * Retrieve the template file from the specified path
     *
     * @param templatePath template path
     */
    private void loadTemplateFile(String templatePath) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(templatePath), "*.yml")) {
            for (Path entry : stream) {
                if (!Files.isRegularFile(entry)) {
                    continue;
                }
                try (InputStream inputStream = Files.newInputStream(entry)) {
                    PageTemplateInfo pageTemplateInfo = yaml.loadAs(inputStream, PageTemplateInfo.class);
                    pageTemplateInfoList.add(pageTemplateInfo);
                }
            }
        } catch (IOException e) {
            LOGGER.error("An error occurred while retrieving template file information", e);
        }
    }
}
