/*
 * Copyright 2002-2010 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.gvnix.flex.roo.addon;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.roo.addon.jpa.activerecord.JpaActiveRecordMetadata;
import org.springframework.roo.addon.jpa.activerecord.JpaCrudAnnotationValues;
import org.springframework.roo.addon.web.mvc.controller.details.FinderMetadataDetails;
import org.springframework.roo.classpath.PhysicalTypeIdentifierNamingUtils;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.details.FieldMetadata;
import org.springframework.roo.classpath.details.MethodMetadata;
import org.springframework.roo.classpath.details.MethodMetadataBuilder;
import org.springframework.roo.classpath.details.annotations.AnnotatedJavaType;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadata;
import org.springframework.roo.classpath.itd.AbstractItdTypeDetailsProvidingMetadataItem;
import org.springframework.roo.classpath.itd.InvocableMemberBodyBuilder;
import org.springframework.roo.classpath.itd.ItdSourceFileComposer;
import org.springframework.roo.classpath.persistence.PersistenceMemberLocator;
import org.springframework.roo.metadata.MetadataIdentificationUtils;
import org.springframework.roo.model.DataType;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.LogicalPath;

/**
 * Metadata for a scaffolded Flex remoting destination.
 * 
 * @author Jeremy Grelle
 */
public class FlexScaffoldMetadata extends
        AbstractItdTypeDetailsProvidingMetadataItem {

    private static final String PROVIDES_TYPE_STRING = FlexScaffoldMetadata.class
            .getName();

    private static final String PROVIDES_TYPE = MetadataIdentificationUtils
            .create(PROVIDES_TYPE_STRING);

    private JpaActiveRecordMetadata entityMetadata;

    private JavaType entity;

    private String entityReference;

    private PersistenceMemberLocator persistenceMemberLocator;

    public FlexScaffoldMetadata(String identifier, JavaType aspectName,
            PhysicalTypeMetadata governorPhysicalTypeMetadata,
            FlexScaffoldAnnotationValues annotationValues,
            JpaActiveRecordMetadata entityMetadata,
            Set<FinderMetadataDetails> dynamicFinderMethods,
            PersistenceMemberLocator persistenceMemberLocator) {

        super(identifier, aspectName, governorPhysicalTypeMetadata);

        Validate.isTrue(isValid(identifier), "Metadata identification string '"
                + identifier + "' does not appear to be a valid");
        Validate.notNull(entityMetadata, "Entity metadata required");

        if (!isValid()) {
            return;
        }

        this.entityMetadata = entityMetadata;
        this.entity = annotationValues.getEntity();
        this.entityReference = StringUtils.uncapitalize(this.entity
                .getSimpleTypeName());
        this.persistenceMemberLocator = persistenceMemberLocator;

        this.builder.addMethod(getCreateMethod());
        this.builder.addMethod(getShowMethod());
        this.builder.addMethod(getListMethod());
        this.builder.addMethod(getListPagedMethod());
        this.builder.addMethod(getUpdateMethod());
        this.builder.addMethod(getRemoveMethod());

        this.itdTypeDetails = this.builder.build();

        new ItdSourceFileComposer(this.itdTypeDetails);
    }

    public static final String getMetadataIdentiferType() {
        return PROVIDES_TYPE;
    }

    public static final String createIdentifier(JavaType javaType,
            LogicalPath path) {
        return PhysicalTypeIdentifierNamingUtils.createIdentifier(
                PROVIDES_TYPE_STRING, javaType, path);
    }

    public static final JavaType getJavaType(String metadataIdentificationString) {
        return PhysicalTypeIdentifierNamingUtils.getJavaType(
                PROVIDES_TYPE_STRING, metadataIdentificationString);
    }

    public JpaActiveRecordMetadata getEntityMetadata() {
        return this.entityMetadata;
    }

    public JavaType getEntity() {
        return this.entity;
    }

    public String getEntityReference() {
        return this.entityReference;
    }

    public static final LogicalPath getPath(String metadataIdentificationString) {
        return PhysicalTypeIdentifierNamingUtils.getPath(PROVIDES_TYPE_STRING,
                metadataIdentificationString);
    }

    public static boolean isValid(String metadataIdentificationString) {
        return PhysicalTypeIdentifierNamingUtils.isValid(PROVIDES_TYPE_STRING,
                metadataIdentificationString);
    }

    private MethodMetadata getRemoveMethod() {
        JavaSymbolName methodName = new JavaSymbolName("remove");

        MethodMetadata method = methodExists(methodName);

        if (method != null) {
            return method;
        }

        // TODO In Roo 1.2.2 migration the identifier fields are multiple:
        // temporary get first element
        FieldMetadata identifierField = persistenceMemberLocator
                .getIdentifierFields(entity).get(0);

        List<AnnotatedJavaType> paramTypes = new ArrayList<AnnotatedJavaType>();
        paramTypes.add(new AnnotatedJavaType(identifierField.getFieldType(),
                new ArrayList<AnnotationMetadata>()));

        List<JavaSymbolName> paramNames = new ArrayList<JavaSymbolName>();
        paramNames.add(new JavaSymbolName(identifierField.getFieldName()
                .getSymbolName()));

        InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
        if (!identifierField.getFieldType().isPrimitive()) {
            bodyBuilder
                    .appendFormalLine("if ("
                            + identifierField.getFieldName().getSymbolName()
                            + " == null) throw new IllegalArgumentException(\"An Identifier is required\");");
        }

        bodyBuilder.appendFormalLine(this.entity
                .getNameIncludingTypeParameters(false,
                        this.builder.getImportRegistrationResolver())
                + "."
                + this.entityMetadata.getFindMethod().getMethodName()
                + "("
                + identifierField.getFieldName().getSymbolName()
                + ")."
                + new JpaCrudAnnotationValues(this.entityMetadata)
                        .getRemoveMethod() + "();");

        return new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName,
                JavaType.VOID_PRIMITIVE, paramTypes, paramNames, bodyBuilder)
                .build();
    }

    private MethodMetadata getListMethod() {
        JavaSymbolName methodName = new JavaSymbolName("list");

        MethodMetadata method = methodExists(methodName);
        if (method != null) {
            return method;
        }

        List<JavaType> typeParams = new ArrayList<JavaType>();
        typeParams.add(this.entity);
        JavaType returnType = new JavaType("java.util.List", 0, DataType.TYPE,
                null, typeParams);

        InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
        bodyBuilder.appendFormalLine("return "
                + this.entity.getNameIncludingTypeParameters(false,
                        this.builder.getImportRegistrationResolver())
                + "."
                + new JpaCrudAnnotationValues(this.entityMetadata)
                        .getFindAllMethod().concat(
                                this.entityMetadata.getPlural()) + "();");
        return new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName,
                returnType, null, null, bodyBuilder).build();
    }

    private MethodMetadata getListPagedMethod() {
        JavaSymbolName methodName = new JavaSymbolName("listPaged");

        MethodMetadata method = methodExists(methodName);
        if (method != null) {
            return method;
        }

        List<JavaType> typeParams = new ArrayList<JavaType>();
        typeParams.add(this.entity);
        JavaType returnType = new JavaType("java.util.List", 0, DataType.TYPE,
                null, typeParams);

        List<AnnotatedJavaType> paramTypes = new ArrayList<AnnotatedJavaType>();
        paramTypes.add(new AnnotatedJavaType(new JavaType("Integer"),
                new ArrayList<AnnotationMetadata>()));
        paramTypes.add(new AnnotatedJavaType(new JavaType("Integer"),
                new ArrayList<AnnotationMetadata>()));

        List<JavaSymbolName> paramNames = new ArrayList<JavaSymbolName>();
        paramNames.add(new JavaSymbolName("page"));
        paramNames.add(new JavaSymbolName("size"));

        InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
        bodyBuilder.appendFormalLine("if (page != null || size != null) {");
        bodyBuilder.indent();
        bodyBuilder
                .appendFormalLine("int sizeNo = size == null ? 10 : size.intValue();");
        bodyBuilder
                .appendFormalLine("return "
                        + this.entity.getNameIncludingTypeParameters(false,
                                this.builder.getImportRegistrationResolver())
                        + "."
                        + new JpaCrudAnnotationValues(this.entityMetadata)
                                .getFindEntriesMethod()
                                .concat(this.entityMetadata.getEntityName())
                                .concat("Entries")
                        + "(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo);");
        bodyBuilder.indentRemove();
        bodyBuilder.appendFormalLine("} else {");
        bodyBuilder.indent();
        bodyBuilder.appendFormalLine("return list();");
        bodyBuilder.indentRemove();
        bodyBuilder.appendFormalLine("}");

        return new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName,
                returnType, paramTypes, paramNames, bodyBuilder).build();
    }

    private MethodMetadata getShowMethod() {
        JavaSymbolName methodName = new JavaSymbolName("show");

        MethodMetadata method = methodExists(methodName);
        if (method != null) {
            return method;
        }

        // TODO In Roo 1.2.2 migration the identifier fields are multiple:
        // temporary get first element
        FieldMetadata identifierField = persistenceMemberLocator
                .getIdentifierFields(entity).get(0);

        List<AnnotatedJavaType> paramTypes = new ArrayList<AnnotatedJavaType>();
        paramTypes.add(new AnnotatedJavaType(identifierField.getFieldType(),
                new ArrayList<AnnotationMetadata>()));

        List<JavaSymbolName> paramNames = new ArrayList<JavaSymbolName>();
        paramNames.add(new JavaSymbolName(identifierField.getFieldName()
                .getSymbolName()));

        InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
        if (!identifierField.getFieldType().isPrimitive()) {
            bodyBuilder
                    .appendFormalLine("if ("
                            + identifierField.getFieldName().getSymbolName()
                            + " == null) throw new IllegalArgumentException(\"An Identifier is required\");");
        }
        bodyBuilder.appendFormalLine("return "
                + this.entity.getNameIncludingTypeParameters(false,
                        this.builder.getImportRegistrationResolver()) + "."
                + this.entityMetadata.getFindMethod().getMethodName() + "("
                + identifierField.getFieldName().getSymbolName() + ");");

        return new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName,
                this.entity, paramTypes, paramNames, bodyBuilder).build();
    }

    private MethodMetadata getCreateMethod() {
        JavaSymbolName methodName = new JavaSymbolName("create");

        MethodMetadata method = methodExists(methodName);
        if (method != null) {
            return method;
        }

        List<AnnotatedJavaType> paramTypes = new ArrayList<AnnotatedJavaType>();
        paramTypes.add(new AnnotatedJavaType(this.entity,
                new ArrayList<AnnotationMetadata>()));

        List<JavaSymbolName> paramNames = new ArrayList<JavaSymbolName>();
        paramNames.add(new JavaSymbolName(this.entityReference));

        InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
        bodyBuilder.appendFormalLine(this.entityReference
                + "."
                + new JpaCrudAnnotationValues(this.entityMetadata)
                        .getPersistMethod() + "();");
        bodyBuilder.appendFormalLine("return " + this.entityReference + ";");

        return new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName,
                this.entity, paramTypes, paramNames, bodyBuilder).build();
    }

    private MethodMetadata getUpdateMethod() {
        JavaSymbolName methodName = new JavaSymbolName("update");

        MethodMetadata method = methodExists(methodName);
        if (method != null) {
            return method;
        }

        List<AnnotatedJavaType> paramTypes = new ArrayList<AnnotatedJavaType>();
        paramTypes.add(new AnnotatedJavaType(this.entity,
                new ArrayList<AnnotationMetadata>()));

        List<JavaSymbolName> paramNames = new ArrayList<JavaSymbolName>();
        paramNames.add(new JavaSymbolName(this.entityReference));

        InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
        bodyBuilder.appendFormalLine("if (" + this.entityReference
                + " == null) throw new IllegalArgumentException(\"A "
                + this.entityReference + " is required\");");
        // fix for https://jira.springsource.org/browse/ROOFLEX-16
        bodyBuilder.appendFormalLine(this.entityReference
                + "="
                + this.entityReference
                + "."
                + new JpaCrudAnnotationValues(this.entityMetadata)
                        .getMergeMethod() + "();");
        bodyBuilder.appendFormalLine("return " + this.entityReference + ";");

        return new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName,
                this.entity, paramTypes, paramNames, bodyBuilder).build();
    }

    private MethodMetadata methodExists(JavaSymbolName methodName) {
        // We have no access to method parameter information, so we scan by name
        // alone and treat any match as
        // authoritative
        // We do not scan the superclass, as the caller is expected to know
        // we'll only scan the current class
        for (MethodMetadata method : this.governorTypeDetails
                .getDeclaredMethods()) {
            if (method.getMethodName().equals(methodName)) {
                // Found a method of the expected name; we won't check method
                // parameters though
                return method;
            }
        }
        return null;
    }
}
