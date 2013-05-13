/*
 * gvNIX. Spring Roo based RAD tool for Generalitat Valenciana Copyright (C)
 * 2013 Generalitat Valenciana
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see &lt;http://www.gnu.org/copyleft/gpl.html&gt;.
 */
package org.gvnix.addon.datatables;

import static org.gvnix.addon.datatables.DatatablesConstants.*;
import static org.springframework.roo.model.JdkJavaType.LIST;
import static org.springframework.roo.model.SpringJavaType.MODEL;
import static org.springframework.roo.model.SpringJavaType.MODEL_ATTRIBUTE;
import static org.springframework.roo.model.SpringJavaType.REQUEST_MAPPING;
import static org.springframework.roo.model.SpringJavaType.RESPONSE_BODY;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.gvnix.addon.web.mvc.batch.WebJpaBatchMetadata;
import org.gvnix.support.WebItdBuilderHelper;
import org.springframework.roo.classpath.PhysicalTypeIdentifierNamingUtils;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.details.FieldMetadata;
import org.springframework.roo.classpath.details.FieldMetadataBuilder;
import org.springframework.roo.classpath.details.MemberFindingUtils;
import org.springframework.roo.classpath.details.MethodMetadata;
import org.springframework.roo.classpath.details.MethodMetadataBuilder;
import org.springframework.roo.classpath.details.annotations.AnnotatedJavaType;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadataBuilder;
import org.springframework.roo.classpath.itd.AbstractItdTypeDetailsProvidingMetadataItem;
import org.springframework.roo.classpath.itd.InvocableMemberBodyBuilder;
import org.springframework.roo.metadata.MetadataIdentificationUtils;
import org.springframework.roo.model.DataType;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.model.JdkJavaType;
import org.springframework.roo.project.LogicalPath;
import org.springframework.roo.support.logging.HandlerUtils;

/**
 * {@link GvNIXDatatables} metadata
 * 
 * @author gvNIX Team
 * @since 1.1.0
 */
public class DatatablesMetadata extends
        AbstractItdTypeDetailsProvidingMetadataItem {

    private static final Logger LOGGER = HandlerUtils
            .getLogger(DatatablesMetadata.class);

    // Constants
    private static final String PROVIDES_TYPE_STRING = DatatablesMetadata.class
            .getName();
    private static final String PROVIDES_TYPE = MetadataIdentificationUtils
            .create(PROVIDES_TYPE_STRING);

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

    public static final LogicalPath getPath(String metadataIdentificationString) {
        return PhysicalTypeIdentifierNamingUtils.getPath(PROVIDES_TYPE_STRING,
                metadataIdentificationString);
    }

    public static boolean isValid(String metadataIdentificationString) {
        return PhysicalTypeIdentifierNamingUtils.isValid(PROVIDES_TYPE_STRING,
                metadataIdentificationString);
    }

    /**
     * Annotation values
     */
    private final DatatablesAnnotationValues annotationValues;

    /**
     * Entity properties which compound its identifier
     */
    private final List<FieldMetadata> identifierProperties;

    /**
     * Related entity
     */
    private final JavaType entity;

    /**
     * Related entity plural
     */
    private final String entityPlural;

    /**
     * Method name to get entity manager from entity class
     */
    private final JavaSymbolName entityEntityManagerMethod;

    /**
     * If related entity has date types
     */
    private final boolean entityHasDateTypes;

    /**
     * Batch services metadata
     */
    private final WebJpaBatchMetadata webJpaBatchMetadata;

    /**
     * Field which holds conversionService
     */
    private FieldMetadata conversionService;

    /**
     * Field which holds current data mode
     */
    private FieldMetadata dataMode;

    /**
     * Itd builder herlper
     */
    private WebItdBuilderHelper helper;

    /**
     * Field which if batch support is available
     */
    private FieldMetadata batchSupport;

    public DatatablesMetadata(String identifier, JavaType aspectName,
            PhysicalTypeMetadata governorPhysicalTypeMetadata,
            DatatablesAnnotationValues annotationValues, JavaType entity,
            List<FieldMetadata> identifierProperties, String entityPlural,
            JavaSymbolName entityManagerMethodName, boolean hasDateTypes,
            JavaType webScaffoldAspectName,
            WebJpaBatchMetadata webJpaBatchMetadata) {
        super(identifier, aspectName, governorPhysicalTypeMetadata);
        Validate.isTrue(isValid(identifier), "Metadata identification string '"
                + identifier + "' does not appear to be a valid");

        this.annotationValues = annotationValues;

        this.helper = new WebItdBuilderHelper(this,
                builder.getImportRegistrationResolver());

        this.identifierProperties = Collections
                .unmodifiableList(identifierProperties);
        this.entity = entity;
        this.entityPlural = entityPlural;
        this.entityEntityManagerMethod = entityManagerMethodName;
        this.entityHasDateTypes = hasDateTypes;
        this.webJpaBatchMetadata = webJpaBatchMetadata;

        // Adding precedence declaration
        // This aspect before webScaffold
        builder.setDeclarePrecedence(aspectName, webScaffoldAspectName);

        // Adding field definition
        builder.addField(getConversionServiceField());
        builder.addField(getUseAjaxField());
        builder.addField(getHasBatchSupportField());

        // Adding methods definition
        builder.addMethod(getRenderForDatatablesMethod());
        builder.addMethod(getGetDatatablesDataMethod());
        builder.addMethod(getListDatatablesRequestMethod());
        builder.addMethod(getListRooRequestMethod());
        builder.addMethod(getPopulateAJAXDatatablesMethod());
        builder.addMethod(getPopulateHasBatchSupportMethod());

        // Create a representation of the desired output ITD
        itdTypeDetails = builder.build();
    }

    /**
     * Gets <code>populateDatatablesHasBatchSupport</code> method
     * 
     * @return
     */
    private MethodMetadata getPopulateHasBatchSupportMethod() {
        // Define method parameter types
        List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();

        // Check if a method with the same signature already exists in the
        // target type
        final MethodMetadata method = methodExists(POPULATE_BATCH_SUPPORT,
                parameterTypes);
        if (method != null) {
            // If it already exists, just return the method and omit its
            // generation via the ITD
            return method;
        }

        // Define method annotations
        List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

        AnnotationMetadataBuilder annotation = new AnnotationMetadataBuilder(
                MODEL_ATTRIBUTE);
        annotation.addStringAttribute("value", "datatablesHasBatchSupport");
        // @ModelAttribute
        annotations.add(annotation);

        // Define method throws types (none in this case)
        List<JavaType> throwsTypes = new ArrayList<JavaType>();

        // Define method parameter names
        List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();

        // Create the method body
        InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
        bodyBuilder.appendFormalLine(String.format("return %s;",
                getHasBatchSupportField().getFieldName().getSymbolName()));

        // Use the MethodMetadataBuilder for easy creation of MethodMetadata
        MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(
                getId(), Modifier.PUBLIC, POPULATE_BATCH_SUPPORT,
                JavaType.BOOLEAN_PRIMITIVE, parameterTypes, parameterNames,
                bodyBuilder);
        methodBuilder.setAnnotations(annotations);
        methodBuilder.setThrowsTypes(throwsTypes);

        return methodBuilder.build(); // Build and return a MethodMetadata
        // instance
    }

    /**
     * Gets <code>populateAJAXDatatables</code> method
     * 
     * @return
     */
    private MethodMetadata getPopulateAJAXDatatablesMethod() {
        // Define method parameter types
        List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();

        // Check if a method with the same signature already exists in the
        // target type
        final MethodMetadata method = methodExists(POPULATE_AJAX_DATATABLES,
                parameterTypes);
        if (method != null) {
            // If it already exists, just return the method and omit its
            // generation via the ITD
            return method;
        }

        // Define method annotations
        List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

        AnnotationMetadataBuilder annotation = new AnnotationMetadataBuilder(
                MODEL_ATTRIBUTE);
        annotation.addStringAttribute("value", "datatablesUseAjax");
        // @ModelAttribute
        annotations.add(annotation);

        // Define method throws types (none in this case)
        List<JavaType> throwsTypes = new ArrayList<JavaType>();

        // Define method parameter names
        List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();

        // Create the method body
        InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
        bodyBuilder.appendFormalLine(String.format("return %s;",
                getUseAjaxField().getFieldName().getSymbolName()));

        // Use the MethodMetadataBuilder for easy creation of MethodMetadata
        MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(
                getId(), Modifier.PUBLIC, POPULATE_AJAX_DATATABLES,
                JavaType.BOOLEAN_PRIMITIVE, parameterTypes, parameterNames,
                bodyBuilder);
        methodBuilder.setAnnotations(annotations);
        methodBuilder.setThrowsTypes(throwsTypes);

        return methodBuilder.build(); // Build and return a MethodMetadata
                                      // instance
    }

    /**
     * Returns <code>listDatatablesRequest</code> method
     * 
     * @return
     */
    private MethodMetadata getListDatatablesRequestMethod() {
        // Define method parameter types
        List<AnnotatedJavaType> parameterTypes = AnnotatedJavaType
                .convertFromJavaTypes(MODEL);

        // Check if a method with the same signature already exists in the
        // target type
        final MethodMetadata method = methodExists(LIST_DATATABLES,
                parameterTypes);
        if (method != null) {
            // If it already exists, just return the method and omit its
            // generation via the ITD
            return method;
        }

        // Define method annotations
        List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

        // @RequestMapping
        AnnotationMetadataBuilder methodAnnotation = new AnnotationMetadataBuilder();
        methodAnnotation.setAnnotationType(REQUEST_MAPPING);

        // @RequestMapping(method = RequestMethod.GET...
        methodAnnotation.addEnumAttribute("method", REQUEST_METHOD, "GET");

        // @RequestMapping(... produces = "text/html")
        methodAnnotation.addStringAttribute("produces", "text/html");

        annotations.add(methodAnnotation);

        // Define method throws types (none in this case)
        List<JavaType> throwsTypes = new ArrayList<JavaType>();

        // Define method parameter names
        List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
        parameterNames.add(UI_MODEL);

        // Create the method body
        InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
        buildListDatatablesRequesMethodBody(bodyBuilder);

        // Use the MethodMetadataBuilder for easy creation of MethodMetadata
        MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(
                getId(), Modifier.PUBLIC, LIST_DATATABLES, JavaType.STRING,
                parameterTypes, parameterNames, bodyBuilder);
        methodBuilder.setAnnotations(annotations);
        methodBuilder.setThrowsTypes(throwsTypes);

        return methodBuilder.build(); // Build and return a MethodMetadata
        // instance
    }

    /**
     * Build method body for <code>listDatatablesRequest</code> method
     * 
     * @param bodyBuilder
     */
    private void buildListDatatablesRequesMethodBody(
            InvocableMemberBodyBuilder bodyBuilder) {

        JavaType objectList = new JavaType(LIST.getFullyQualifiedTypeName(), 0,
                DataType.TYPE, null, Arrays.asList(entity));
        String listVarName = StringUtils.uncapitalize(entityPlural);

        bodyBuilder.appendFormalLine(String.format("if (!%s) {",
                getUseAjaxField().getFieldName().getSymbolName()));
        bodyBuilder.indent();
        bodyBuilder
                .appendFormalLine("// Get all data to put it on pageContext");
        bodyBuilder.appendFormalLine(String.format("%s %s = %s.findAll%s();",
                helper.getFinalTypeName(objectList), listVarName,
                helper.getFinalTypeName(entity), entityPlural));

        // uiModel.addAttribute("pets",pets);
        bodyBuilder.appendFormalLine(String.format(
                "%s.addAttribute(\"%s\",%s);", UI_MODEL.getSymbolName(),
                entityPlural.toLowerCase(), listVarName));
        bodyBuilder.indentRemove();
        bodyBuilder.appendFormalLine("}");

        // return "pets/list";
        bodyBuilder.appendFormalLine(String.format("return \"%s/list\";",
                entityPlural.toLowerCase()));

    }

    /**
     * Redefines {@code list} Roo webScaffod method to delegate on
     * {@link #LIST_DATATABLES}
     * 
     * @return
     */
    private MethodMetadata getListRooRequestMethod() {

        // public String OwnerController.list(
        // @RequestParam(value = "page", required = false) Integer page,
        // @RequestParam(value = "size", required = false) Integer size,
        // Model uiModel) {

        // Define method parameter types
        final List<AnnotatedJavaType> parameterTypes = Arrays.asList(helper
                .createRequestParam(JavaType.INT_OBJECT, "page", false, null),
                helper.createRequestParam(JavaType.INT_OBJECT, "size", false,
                        null), new AnnotatedJavaType(MODEL));

        // Check if a method with the same signature already exists in the
        // target type
        final MethodMetadata method = methodExists(LIST_ROO, parameterTypes);
        if (method != null) {
            // If it already exists, just return the method and omit its
            // generation via the ITD
            return method;
        }

        // Define method annotations
        List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

        // @RequestMapping(produces = "text/html")
        annotations.add(helper.getRequestMappingAnnotation(null, null, null,
                "text/html", null, null));

        // Define method throws types (none in this case)
        List<JavaType> throwsTypes = new ArrayList<JavaType>();

        // Define method parameter names
        final List<JavaSymbolName> parameterNames = Arrays.asList(
                new JavaSymbolName("page"), new JavaSymbolName("size"),
                UI_MODEL);

        // Create the method body
        InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
        buildListRooRequestMethodBody(bodyBuilder);

        // Use the MethodMetadataBuilder for easy creation of MethodMetadata
        MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(
                getId(), Modifier.PUBLIC, LIST_ROO, JavaType.STRING,
                parameterTypes, parameterNames, bodyBuilder);
        methodBuilder.setAnnotations(annotations);
        methodBuilder.setThrowsTypes(throwsTypes);

        return methodBuilder.build(); // Build and return a MethodMetadata
                                      // instance
    }

    /**
     * Build method body for <code>listRooRequest</code> method
     * 
     * @param bodyBuilder
     */
    private void buildListRooRequestMethodBody(
            InvocableMemberBodyBuilder bodyBuilder) {

        bodyBuilder
                .appendFormalLine("// overrides the standar Roo list method and");
        bodyBuilder.appendFormalLine("// delegates on datatables list method");
        // return listDatatables(uiModel);
        bodyBuilder.appendFormalLine(String.format("return %s(%s);",
                LIST_DATATABLES.getSymbolName(), UI_MODEL.getSymbolName()));
    }

    /**
     * Returns <code>getDatatablesData</code> method
     * 
     * @return
     */
    private MethodMetadata getGetDatatablesDataMethod() {
        // Define method parameter types
        List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();

        parameterTypes.add(new AnnotatedJavaType(DATATABLES_CRITERIA_TYPE,
                new AnnotationMetadataBuilder(DATATABLES_PARAMS).build()));
        parameterTypes.add(AnnotatedJavaType.convertFromJavaType(MODEL));

        // Check if a method with the same signature already exists in the
        // target type
        final MethodMetadata method = methodExists(GET_DATATABLES_DATA,
                parameterTypes);
        if (method != null) {
            // If it already exists, just return the method and omit its
            // generation via the ITD
            return method;
        }

        // Define method annotations
        List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();
        AnnotationMetadataBuilder methodAnnotation = new AnnotationMetadataBuilder();
        methodAnnotation.setAnnotationType(REQUEST_MAPPING);
        methodAnnotation.addStringAttribute("headers",
                "Accept=application/json");
        methodAnnotation.addStringAttribute("value", "/datatables/ajax");
        methodAnnotation.addStringAttribute("produces", "application/json");
        annotations.add(methodAnnotation);
        annotations.add(new AnnotationMetadataBuilder(RESPONSE_BODY));

        // Define method throws types (none in this case)
        List<JavaType> throwsTypes = new ArrayList<JavaType>();

        // Define method parameter names
        List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
        parameterNames.add(CRITERIA_PARAM_NAME);
        parameterNames.add(UI_MODEL);

        // Create the method body
        InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
        buildGetDatatablesDataMethodBody(bodyBuilder);

        // Use the MethodMetadataBuilder for easy creation of MethodMetadata
        MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(
                getId(), Modifier.PUBLIC, GET_DATATABLES_DATA,
                GET_DATATABLES_DATA_RETURN, parameterTypes, parameterNames,
                bodyBuilder);
        methodBuilder.setAnnotations(annotations);
        methodBuilder.setThrowsTypes(throwsTypes);

        return methodBuilder.build(); // Build and return a MethodMetadata
                                      // instance
    }

    /**
     * Build method body for <code>getDatatablesData</code> method
     * 
     * @param bodyBuilder
     */
    private void buildGetDatatablesDataMethodBody(
            InvocableMemberBodyBuilder bodyBuilder) {
        // List<Map<String, String>> renderedPets = new ArrayList<Map<String,
        // String>>(0);;
        bodyBuilder.appendFormalLine(String.format("%s rendered = new %s(0);",
                helper.getFinalTypeName(LIST_MAP_STRING_STRING),
                helper.getFinalTypeName(RENDER_FOR_DATATABLES_RETURN_IMP)));

        JavaType findResult = new JavaType(
                DATATABLES_UTILS_RESULT.getFullyQualifiedTypeName(), 0,
                DataType.TYPE, null, Arrays.asList(entity));

        // FindResult<Pet> findResult =
        // DatatablesUtils.findByCriteria(Pet.class, Pet.entityManager(),
        // criterias);
        bodyBuilder.appendFormalLine(String.format(
                "%s findResult = %s.findByCriteria(%s.class, %s.%s(), %s);",
                helper.getFinalTypeName(findResult),
                helper.getFinalTypeName(DATATABLES_UTILS),
                helper.getFinalTypeName(entity),
                helper.getFinalTypeName(entity),
                entityEntityManagerMethod.getSymbolName(),
                CRITERIA_PARAM_NAME.getSymbolName()));

        JavaType objectList = new JavaType(LIST.getFullyQualifiedTypeName(), 0,
                DataType.TYPE, null, Arrays.asList(entity));
        // List<Pet> petList = findResult.getResult();
        String itemListVar = StringUtils.uncapitalize(
                entity.getSimpleTypeName()).concat("List");
        bodyBuilder.appendFormalLine(String.format(
                "%s %s = findResult.getResult();",
                helper.getFinalTypeName(objectList), itemListVar));
        // if (petList != null) {
        bodyBuilder.appendFormalLine(String.format("if (%s != null) {",
                itemListVar));
        bodyBuilder.indent();
        // rendered = renderForDatatables(petList,criterias,uiModel);
        bodyBuilder.appendFormalLine(String.format("rendered = %s(%s,%s,%s);",
                RENDER_FOR_DATATABLES.getSymbolName(), itemListVar,
                CRITERIA_PARAM_NAME.getSymbolName(), UI_MODEL.getSymbolName()));

        bodyBuilder.indentRemove();
        bodyBuilder.appendFormalLine("}");

        // long recordsFound = findResult.getTotalResultCount();
        bodyBuilder
                .appendFormalLine("long recordsFound = findResult.getTotalResultCount();");

        JavaType dataSet = new JavaType(
                "com.github.dandelion.datatables.core.ajax.DataSet", 0,
                DataType.TYPE, null, Arrays.asList(MAP_STRING_STRING));

        // DataSet<Map<String, String>> dataSet = new DataSet<Map<String,
        // String>>(rendered, Pet.countPets(), recordsFound);
        bodyBuilder.appendFormalLine(String.format(
                "%s dataSet =  new %1$s(rendered,%s.count%s(),recordsFound);",
                helper.getFinalTypeName(dataSet),
                helper.getFinalTypeName(entity), entityPlural));

        // return DatatablesResponse.build(dataSet, criterias);
        bodyBuilder.appendFormalLine(String.format(
                "return %s.build(dataSet,%s);",
                helper.getFinalTypeName(DATATABLES_RESPONSE),
                CRITERIA_PARAM_NAME.getSymbolName()));
    }

    /**
     * Returns <code>renderForDatatables</code> method
     * 
     * @return
     */
    private MethodMetadata getRenderForDatatablesMethod() {
        // Define method parameter types
        JavaType objectList = new JavaType(LIST.getFullyQualifiedTypeName(), 0,
                DataType.TYPE, null, Arrays.asList(entity));
        List<AnnotatedJavaType> parameterTypes = AnnotatedJavaType
                .convertFromJavaTypes(objectList, DATATABLES_CRITERIA_TYPE,
                        MODEL);

        // Check if a method with the same signature already exists in the
        // target type
        final MethodMetadata method = methodExists(RENDER_FOR_DATATABLES,
                parameterTypes);
        if (method != null) {
            // If it already exists, just return the method and omit its
            // generation via the ITD
            return method;
        }

        // Define method annotations (none in this case)
        List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

        // Define method throws types (none in this case)
        List<JavaType> throwsTypes = new ArrayList<JavaType>();

        // Define method parameter names
        List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
        parameterNames.add(ITEM_LIST_PARAM_NAME);
        parameterNames.add(CRITERIA_PARAM_NAME);
        parameterNames.add(UI_MODEL);

        // Create the method body
        InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
        buildRenderForDatatablesMethodBody(bodyBuilder);

        // Use the MethodMetadataBuilder for easy creation of MethodMetadata
        MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(
                getId(), Modifier.PUBLIC, RENDER_FOR_DATATABLES,
                RENDER_FOR_DATATABLES_RETURN, parameterTypes, parameterNames,
                bodyBuilder);
        methodBuilder.setAnnotations(annotations);
        methodBuilder.setThrowsTypes(throwsTypes);

        return methodBuilder.build(); // Build and return a MethodMetadata
                                      // instance
    }

    /**
     * Build method body for <code>renderForDatatables</code> method
     * 
     * @param bodyBuilder
     */
    private void buildRenderForDatatablesMethodBody(
            InvocableMemberBodyBuilder bodyBuilder) {
        // Get logger to trace data load problems
        // Logger logger = Logger.getLogger(getClass().getName());
        bodyBuilder.appendFormalLine(String.format(
                "%1$s logger = %1$s.getLogger(getClass().getName());",
                helper.getFinalTypeName(LOGGER_TYPE)));

        bodyBuilder.appendFormalLine("");
        bodyBuilder.appendFormalLine("// Prepare result var");
        // List<Map<String, String>> result = new
        // ArrayList<Map<String,String>>(pets.size());
        bodyBuilder.appendFormalLine(String.format(
                "%s result = new %s(%s.size());",
                helper.getFinalTypeName(RENDER_FOR_DATATABLES_RETURN),
                helper.getFinalTypeName(RENDER_FOR_DATATABLES_RETURN_IMP),
                ITEM_LIST_PARAM_NAME.getSymbolName()));

        // Roo use only one filed as pk
        // (if it's composite use a embeddedPk)
        bodyBuilder.appendFormalLine("// Prepare primaryKey fields");
        // String pkFieldName = "id";
        bodyBuilder.appendFormalLine(String.format("%s pkFieldName = \"%s\";",
                helper.getFinalTypeName(JavaType.STRING), identifierProperties
                        .get(0).getFieldName().getSymbolName()));

        bodyBuilder.appendFormalLine("");
        bodyBuilder.appendFormalLine("// Prepare required fields");
        // Set<String> fields = new HashSet<String>();
        bodyBuilder.appendFormalLine(String.format("%s fields = new %s();",
                helper.getFinalTypeName(SET_STRING),
                helper.getFinalTypeName(HASHSET_STRING)));
        // fields.add( pkFieldName );
        bodyBuilder.appendFormalLine("fields.add(pkFieldName);");

        bodyBuilder.appendFormalLine("");
        bodyBuilder.appendFormalLine("// Add fields from request");
        // for (ColumnDef colum : criterias.getColumnDefs()){
        bodyBuilder.appendFormalLine(String.format(
                "for (%s colum : %s.getColumnDefs()){",
                helper.getFinalTypeName(DATATABLES_COLUMNDEF),
                CRITERIA_PARAM_NAME.getSymbolName()));
        bodyBuilder.indent();
        // fields.add(colum.getName());
        bodyBuilder.appendFormalLine("fields.add(colum.getName());");
        bodyBuilder.indentRemove();
        bodyBuilder.appendFormalLine("}");

        bodyBuilder.appendFormalLine("");
        bodyBuilder.appendFormalLine("// Date formaters");
        // DateFormat defaultFormat = SimpleDateFormat.getDateInstance();
        bodyBuilder.appendFormalLine(String.format(
                "%s defaultFormat = %s.getDateInstance();",
                helper.getFinalTypeName(DATE_FORMAT),
                helper.getFinalTypeName(SIMPLE_DATE_FORMAT)));

        // If entity has date attributes use addDateTimeFormatPatterns method
        // to initialize formatters
        if (entityHasDateTypes) {
            bodyBuilder
                    .appendFormalLine(String.format(
                            "addDateTimeFormatPatterns(%s);",
                            UI_MODEL.getSymbolName()));
        }

        bodyBuilder.appendFormalLine("");
        bodyBuilder.appendFormalLine("// Load result");
        // Map<String, String> rendered = null;
        bodyBuilder.appendFormalLine(String.format("%s rendered = null;",
                helper.getFinalTypeName(MAP_STRING_STRING)));
        String itemVar = StringUtils.uncapitalize(entity.getSimpleTypeName());
        // for (Pet pet : pets) {
        bodyBuilder.appendFormalLine(String.format("for (%s %s : %s) {",
                helper.getFinalTypeName(entity), itemVar,
                ITEM_LIST_PARAM_NAME.getSymbolName()));
        bodyBuilder.indent();
        // rendered = new HashMap<String, String>(fields.size());
        bodyBuilder.appendFormalLine(String.format(
                "rendered = new %s(fields.size());",
                helper.getFinalTypeName(HASHMAP_STRING_STRING)));
        String itemVarBean = itemVar.concat("Bean");
        // BeanWrapper petBean = new BeanWrapperImpl(pet);
        bodyBuilder
                .appendFormalLine(String.format("%s %s = new %s(%s);", helper
                        .getFinalTypeName(new JavaType(
                                "org.springframework.beans.BeanWrapper")),
                        itemVarBean, helper.getFinalTypeName(new JavaType(
                                "org.springframework.beans.BeanWrapperImpl")),
                        itemVar));
        // for (String fieldName : fields){
        bodyBuilder.appendFormalLine("for (String fieldName : fields) {");
        bodyBuilder.indent();

        bodyBuilder.append("");
        // check if property exists (trace it else)
        bodyBuilder
                .appendFormalLine("// check if property exists (trace it else)");
        // if (!petBean.isReadableProperty(fieldName)){
        bodyBuilder.appendFormalLine(String.format(
                "if (!%s.isReadableProperty(fieldName)) {", itemVarBean));
        bodyBuilder.indent();
        // logger.finer("Property '".concat(fieldName).concat("' not found in bean."));
        bodyBuilder
                .appendFormalLine("logger.finer(\"Property '\".concat(fieldName).concat(\"' not found in bean.\"));");
        // continue;
        bodyBuilder.appendFormalLine("continue;");
        bodyBuilder.indentRemove();
        // }
        bodyBuilder.appendFormalLine("}");

        // Object value = null;
        bodyBuilder.appendFormalLine("Object value = null;");

        // String valueStr = null;
        bodyBuilder.appendFormalLine("String valueStr = null;");

        // try {
        bodyBuilder.appendFormalLine("try {");
        bodyBuilder.indent();

        // value = petBean.getPropertyValue(fieldName);
        bodyBuilder.appendFormalLine(String.format(
                "value = %s.getPropertyValue(fieldName);", itemVarBean));

        String conversionService = getConversionServiceField().getFieldName()
                .getSymbolName();

        // if (Calencar.class.isAssignableFrom(value.getClass())) {
        bodyBuilder.appendFormalLine(String.format(
                "if (%s.class.isAssignableFrom(value.getClass())) {",
                helper.getFinalTypeName(JdkJavaType.CALENDAR)));
        bodyBuilder.indent();
        // value = ((Calendar) value).getTime();
        bodyBuilder.appendFormalLine(String.format(
                "value = ((%s) value).getTime();",
                helper.getFinalTypeName(JdkJavaType.CALENDAR)));
        bodyBuilder.indentRemove();
        bodyBuilder.appendFormalLine("}");

        // if (Date.class.isAssignableFrom(value.getClass())) {
        bodyBuilder.appendFormalLine(String.format(
                "if (%s.class.isAssignableFrom(value.getClass())) {",
                helper.getFinalTypeName(JdkJavaType.DATE)));
        bodyBuilder.indent();
        if (entityHasDateTypes) {
            // String pattern = (String)
            // uiModel.asMap().get("pet_".concat(fieldName.toLowerCase()).concat("_date_format"));
            bodyBuilder
                    .appendFormalLine(String
                            .format("String pattern = (String) uiModel.asMap().get(\"%s_\".concat(fieldName.toLowerCase()).concat(\"_date_format\"));",
                                    StringUtils.uncapitalize(entity
                                            .getSimpleTypeName())));
            // DateFormat format = StringUtils.isBlank(pattern) ? defaultFormat
            // : new SimpleDateFormat(pattern);
            bodyBuilder
                    .appendFormalLine(String
                            .format("%s format = %s.isBlank(pattern) ? defaultFormat : new %s(pattern);",
                                    helper.getFinalTypeName(DATE_FORMAT),
                                    helper.getFinalTypeName(STRING_UTILS),
                                    helper.getFinalTypeName(SIMPLE_DATE_FORMAT),
                                    StringUtils.uncapitalize(entity
                                            .getSimpleTypeName())));
            // valueStr = format.format(value);
            bodyBuilder.appendFormalLine("valueStr = format.format(value);");
        }
        else {
            // valueStr = defaultFormat.format(value);
            bodyBuilder
                    .appendFormalLine("valueStr = defaultFormat.format(value);");
        }

        bodyBuilder.indentRemove();
        // } else if (conversionService.canConvert(value.getClass(),
        // String.class)) {
        bodyBuilder.appendFormalLine(String.format(
                "} else if (%s.canConvert(value.getClass(), String.class)) {",
                conversionService));
        bodyBuilder.indent();
        // valueStr = conversionService.convert(value, String.class);
        bodyBuilder.appendFormalLine(String.format(
                "valueStr = %s.convert(value, String.class);",
                conversionService));
        bodyBuilder.indentRemove();
        // } else {
        bodyBuilder.appendFormalLine("} else {");
        bodyBuilder.indent();
        JavaType objectUtil = new JavaType(
                "org.springframework.util.ObjectUtils");
        // valueStr = ObjectUtils.getDisplayString(value);
        bodyBuilder.appendFormalLine(String.format(
                "valueStr = %s.getDisplayString(value);",
                helper.getFinalTypeName(objectUtil)));
        bodyBuilder.indentRemove();
        bodyBuilder.appendFormalLine("}");
        bodyBuilder.indentRemove();
        // } catch (Exception e) {
        bodyBuilder.appendFormalLine("} catch (Exception e) {");
        bodyBuilder.indent();

        bodyBuilder.append("");
        // debug getting value problem
        bodyBuilder.appendFormalLine("// debug getting value problem");
        // logger.log(Level.FINE,"Error getting value '".concat(fieldName).concat("'"),e);
        bodyBuilder
                .appendFormalLine(String
                        .format("logger.log(%s.FINE,\"Error getting value '\".concat(fieldName).concat(\"'\"),e);",
                                helper.getFinalTypeName(LOGGER_LEVEL)));

        // } (end catch)
        bodyBuilder.indentRemove();
        bodyBuilder.appendFormalLine("}");

        // rendered.put(fieldName, valueStr);
        bodyBuilder.appendFormalLine("rendered.put(fieldName, valueStr);");

        bodyBuilder.appendFormalLine("");
        bodyBuilder.appendFormalLine("// Set PK value as DT_RowId");
        bodyBuilder
                .appendFormalLine("// Note when entity has composite PK Roo generates the need");
        bodyBuilder
                .appendFormalLine("// convert method and adds it to ConversionService, so");
        bodyBuilder
                .appendFormalLine("// when processed field is the PK the valueStr is the ");
        bodyBuilder
                .appendFormalLine("// composite PK instance marshalled to JSON notation and");
        bodyBuilder.appendFormalLine("// Base64 encoded");
        // if (pkFieldName.equalsIgnoreCase(fieldName)) {
        bodyBuilder
                .appendFormalLine("if (pkFieldName.equalsIgnoreCase(fieldName)) {");
        bodyBuilder.indent();
        // rendered.put(fieldName, valueStr);
        bodyBuilder.appendFormalLine("rendered.put(\"DT_RowId\", valueStr);");
        bodyBuilder.indentRemove();
        bodyBuilder.appendFormalLine("}");
        // }

        bodyBuilder.indentRemove();
        bodyBuilder.appendFormalLine("}");

        bodyBuilder.appendFormalLine("result.add(rendered);");
        bodyBuilder.indentRemove();
        bodyBuilder.appendFormalLine("}");
        bodyBuilder.appendFormalLine("return result;");

        bodyBuilder.reset();
    }

    /**
     * Create metadata for auto-wired convertionService field.
     * 
     * @return a FieldMetadata object
     */
    public FieldMetadata getUseAjaxField() {
        if (dataMode == null) {
            JavaSymbolName curName = new JavaSymbolName("datatablesUseAjax");
            String initializer = String.format("%s",
                    String.valueOf(annotationValues.isAjax()));

            // Check if field exist
            FieldMetadata currentField = governorTypeDetails
                    .getDeclaredField(curName);
            if (currentField != null) {
                if (!currentField.getFieldType().equals(
                        JavaType.BOOLEAN_PRIMITIVE)) {
                    // No compatible field: look for new name
                    currentField = null;
                    JavaSymbolName newName = curName;
                    int i = 1;
                    while (governorTypeDetails.getDeclaredField(newName) != null) {
                        newName = new JavaSymbolName(curName.getSymbolName()
                                .concat(StringUtils.repeat('_', i)));
                        i++;
                    }
                    curName = newName;
                }
            }
            if (currentField != null) {
                // check initializer value
                if (StringUtils.equalsIgnoreCase(initializer.trim(),
                        currentField.getFieldInitializer().trim())) {
                    dataMode = currentField;
                }
                else {
                    // Show a warning
                    LOGGER.warning(String.format(
                            "%s.%s has a different value than %s.ajax",
                            getId(), currentField.getFieldName()
                                    .getReadableSymbolName(),
                            GvNIXDatatables.class.getSimpleName()));
                }
            }
            else {
                // create field
                List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>(
                        0);
                // Using the FieldMetadataBuilder to create the field
                // definition.
                final FieldMetadataBuilder fieldBuilder = new FieldMetadataBuilder(
                        getId(), Modifier.PUBLIC, annotations, curName, // Field
                        JavaType.BOOLEAN_PRIMITIVE); // Field type
                fieldBuilder.setFieldInitializer(initializer);
                dataMode = fieldBuilder.build(); // Build and return a
                // FieldMetadata
                // instance
            }
        }
        return dataMode;
    }

    /**
     * Create metadata for field which informs that batch services available.
     * 
     * @return a FieldMetadata object
     */
    public FieldMetadata getHasBatchSupportField() {
        if (batchSupport == null) {
            JavaSymbolName curName = new JavaSymbolName(
                    "datatablesHasBatchSupport");
            String initializer = String.format("%s",
                    String.valueOf(webJpaBatchMetadata != null));

            // Check if field exist
            FieldMetadata currentField = governorTypeDetails
                    .getDeclaredField(curName);
            if (currentField != null) {
                if (!currentField.getFieldType().equals(
                        JavaType.BOOLEAN_PRIMITIVE)) {
                    // No compatible field: look for new name
                    currentField = null;
                    JavaSymbolName newName = curName;
                    int i = 1;
                    while (governorTypeDetails.getDeclaredField(newName) != null) {
                        newName = new JavaSymbolName(curName.getSymbolName()
                                .concat(StringUtils.repeat('_', i)));
                        i++;
                    }
                    curName = newName;
                }
            }
            if (currentField != null) {
                // check initializer value
                if (StringUtils.equalsIgnoreCase(initializer.trim(),
                        currentField.getFieldInitializer().trim())) {
                    batchSupport = currentField;
                }
                else {
                    // Show a warning
                    LOGGER.warning(String
                            .format("%s.%s is erroneous", getId(), currentField
                                    .getFieldName().getReadableSymbolName()));
                }
            }
            else {
                // create field
                // Using the FieldMetadataBuilder to create the field
                // definition.
                final FieldMetadataBuilder fieldBuilder = new FieldMetadataBuilder(
                        getId(), Modifier.PUBLIC, curName, // Field
                        JavaType.BOOLEAN_PRIMITIVE, initializer); // Field type
                batchSupport = fieldBuilder.build(); // Build and return a
                // FieldMetadata
                // instance
            }
        }
        return batchSupport;
    }

    /**
     * Create metadata for auto-wired convertionService field.
     * 
     * @return a FieldMetadata object
     */
    public FieldMetadata getConversionServiceField() {
        if (conversionService == null) {
            JavaSymbolName curName = new JavaSymbolName(
                    "conversionService_datatables");
            // Check if field exist
            FieldMetadata currentField = governorTypeDetails
                    .getDeclaredField(curName);
            if (currentField != null) {
                if (currentField.getAnnotation(AUTOWIRED) == null
                        || !currentField.getFieldType().equals(
                                CONVERSION_SERVICE)) {
                    // No compatible field: look for new name
                    currentField = null;
                    JavaSymbolName newName = curName;
                    int i = 1;
                    while (governorTypeDetails.getDeclaredField(newName) != null) {
                        newName = new JavaSymbolName(curName.getSymbolName()
                                .concat(StringUtils.repeat('_', i)));
                        i++;
                    }
                    curName = newName;
                }
            }
            if (currentField != null) {
                conversionService = currentField;
            }
            else {
                // create field
                List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>(
                        1);
                annotations.add(new AnnotationMetadataBuilder(AUTOWIRED));
                // Using the FieldMetadataBuilder to create the field
                // definition.
                final FieldMetadataBuilder fieldBuilder = new FieldMetadataBuilder(
                        getId(), Modifier.PUBLIC, annotations, curName, // Field
                        CONVERSION_SERVICE); // Field type
                conversionService = fieldBuilder.build(); // Build and return a
                                                          // FieldMetadata
                // instance
            }
        }
        return conversionService;
    }

    private MethodMetadata methodExists(JavaSymbolName methodName,
            List<AnnotatedJavaType> paramTypes) {

        return MemberFindingUtils.getDeclaredMethod(governorTypeDetails,
                methodName,
                AnnotatedJavaType.convertFromAnnotatedJavaTypes(paramTypes));
    }

    // Typically, no changes are required beyond this point

    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("identifier", getId());
        builder.append("valid", valid);
        builder.append("aspectName", aspectName);
        builder.append("destinationType", destination);
        builder.append("governor", governorPhysicalTypeMetadata.getId());
        builder.append("ajax", String.valueOf(annotationValues.isAjax()));
        builder.append("itdTypeDetails", itdTypeDetails);
        return builder.toString();
    }

    public JavaType getEntity() {
        return entity;
    }

    public DatatablesAnnotationValues getAnnotationValues() {
        return annotationValues;
    }
}
