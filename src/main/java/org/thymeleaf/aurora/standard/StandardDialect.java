/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.thymeleaf.aurora.standard;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.aurora.dialect.AbstractProcessorDialect;
import org.thymeleaf.aurora.dialect.IExecutionAttributesDialect;
import org.thymeleaf.aurora.dialect.IExpressionObjectsDialect;
import org.thymeleaf.aurora.expression.IExpressionObjectsFactory;
import org.thymeleaf.aurora.processor.IProcessor;
import org.thymeleaf.aurora.standard.expression.StandardExpressionObjectsFactory;
import org.thymeleaf.aurora.standard.processor.StandardClassTagProcessor;
import org.thymeleaf.aurora.standard.processor.StandardDefaultAttributesTagProcessor;
import org.thymeleaf.aurora.standard.processor.StandardEachTagProcessor;
import org.thymeleaf.aurora.standard.processor.StandardIncludeTagProcessor;
import org.thymeleaf.aurora.standard.processor.StandardInlineTagProcessor;
import org.thymeleaf.aurora.standard.processor.StandardInliningTextProcessor;
import org.thymeleaf.aurora.standard.processor.StandardObjectTagProcessor;
import org.thymeleaf.aurora.standard.processor.StandardRemoveTagProcessor;
import org.thymeleaf.aurora.standard.processor.StandardReplaceTagProcessor;
import org.thymeleaf.aurora.standard.processor.StandardTextTagProcessor;
import org.thymeleaf.aurora.standard.processor.StandardWithTagProcessor;
import org.thymeleaf.standard.expression.IStandardConversionService;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.IStandardVariableExpressionEvaluator;
import org.thymeleaf.standard.expression.OGNLVariableExpressionEvaluator;
import org.thymeleaf.standard.expression.StandardConversionService;
import org.thymeleaf.standard.expression.StandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 1.0 (reimplemented in 3.0.0)
 *
 */
public class StandardDialect
            extends AbstractProcessorDialect
            implements IExecutionAttributesDialect, IExpressionObjectsDialect {

    private static final String NAME = "Standard";
    private static final String PREFIX = "th";


    // We will avoid setting this variableExpressionEvaluator variable to "OgnlVariableExprtessionEvalutator.INSTANCE"
    // in order to not cause this OGNL-related class to initialize, therefore introducing a forced dependency on OGNL
    // to Spring users (who don't need OGNL at all).
    private IStandardVariableExpressionEvaluator variableExpressionEvaluator = null;
    private IStandardExpressionParser expressionParser = new StandardExpressionParser();
    private IStandardConversionService conversionService = new StandardConversionService();






    public StandardDialect() {
        super(NAME, PREFIX, createStandardProcessorsSet());
    }




    /**
     * <p>
     *   Returns the variable expression evaluator (implementation of {@link IStandardVariableExpressionEvaluator})
     *   that is configured to be used at this instance of the Standard Dialect.
     * </p>
     * <p>
     *   This is used for executing all ${...} and *{...} expressions in Thymeleaf Standard Expressions.
     * </p>
     * <p>
     *   This will be {@link OGNLVariableExpressionEvaluator} by default. When using the Spring Standard
     *   Dialect, this will be a SpringEL-based implementation.
     * </p>
     *
     * @return the Standard Variable Expression Evaluator object.
     * @since 2.1.0
     */
    public IStandardVariableExpressionEvaluator getVariableExpressionEvaluator() {
        if (this.variableExpressionEvaluator == null) {
            return OGNLVariableExpressionEvaluator.INSTANCE;
        }
        return this.variableExpressionEvaluator;
    }


    /**
     * <p>
     *   Sets the variable expression evaluator (implementation of {@link IStandardVariableExpressionEvaluator})
     *   that should be used at this instance of the Standard Dialect.
     * </p>
     * <p>
     *   This is used for executing all ${...} and *{...} expressions in Thymeleaf Standard Expressions.
     * </p>
     * <p>
     *   This will be {@link OGNLVariableExpressionEvaluator#INSTANCE} by default. When using the Spring Standard
     *   Dialect, this will be a SpringEL-based implementation.
     * </p>
     * <p>
     *   This method has no effect once the Template Engine has been initialized.
     * </p>
     * <p>
     *   Objects set here should be <b>thread-safe</b>.
     * </p>
     *
     * @param variableExpressionEvaluator the new Standard Variable Expression Evaluator object. Cannot be null.
     * @since 2.1.0
     */
    public void setVariableExpressionEvaluator(final IStandardVariableExpressionEvaluator variableExpressionEvaluator) {
        Validate.notNull(variableExpressionEvaluator, "Standard Variable Expression Evaluator cannot be null");
        this.variableExpressionEvaluator = variableExpressionEvaluator;
    }


    /**
     * <p>
     *   Returns the Thymeleaf Standard Expression parser (implementation of {@link IStandardExpressionParser})
     *   that is configured to be used at this instance of the Standard Dialect.
     * </p>
     * <p>
     *   This will be {@link StandardExpressionParser} by default.
     * </p>
     *
     * @return the Standard Expression Parser object.
     * @since 2.1.0
     */
    public IStandardExpressionParser getExpressionParser() {
        return this.expressionParser;
    }


    /**
     * <p>
     *   Sets the Thymeleaf Standard Expression parser (implementation of {@link IStandardExpressionParser})
     *   that should be used at this instance of the Standard Dialect.
     * </p>
     * <p>
     *   This will be {@link StandardExpressionParser} by default.
     * </p>
     * <p>
     *   This method has no effect once the Template Engine has been initialized.
     * </p>
     * <p>
     *   Objects set here should be <b>thread-safe</b>.
     * </p>
     *
     * @param expressionParser the Standard Expression Parser object to be used. Cannot be null.
     * @since 2.1.0
     */
    public void setExpressionParser(final IStandardExpressionParser expressionParser) {
        Validate.notNull(expressionParser, "Standard Expression Parser cannot be null");
        this.expressionParser = expressionParser;
    }


    /**
     * <p>
     *   Returns the Standard Conversion Service (implementation of {@link IStandardConversionService})
     *   that is configured to be used at this instance of the Standard Dialect.
     * </p>
     * <p>
     *   This will be {@link StandardConversionService} by default. In Spring environments, this will default
     *   to an implementation delegating on Spring's own ConversionService implementation.
     * </p>
     *
     * @return the Standard Conversion Service object.
     * @since 2.1.0
     */
    public IStandardConversionService getConversionService() {
        return this.conversionService;
    }


    /**
     * <p>
     *   Sets the Standard Conversion Service (implementation of {@link IStandardConversionService})
     *   that should to be used at this instance of the Standard Dialect.
     * </p>
     * <p>
     *   This will be {@link StandardConversionService} by default. In Spring environments, this will default
     *   to an implementation delegating on Spring's own ConversionService implementation.
     * </p>
     * <p>
     *   This method has no effect once the Template Engine has been initialized.
     * </p>
     * <p>
     *   Objects set here should be <b>thread-safe</b>.
     * </p>
     *
     * @param conversionService the Standard ConversionService object to be used. Cannot be null.
     * @since 2.1.0
     */
    public void setConversionService(final IStandardConversionService conversionService) {
        Validate.notNull(conversionService, "Standard Conversion Service cannot be null");
        this.conversionService = conversionService;
    }









    public Map<String, Object> getExecutionAttributes() {

        final Map<String,Object> executionAttributes = new HashMap<String, Object>(5, 1.0f);
        executionAttributes.put(
                StandardExpressions.STANDARD_VARIABLE_EXPRESSION_EVALUATOR_ATTRIBUTE_NAME, getVariableExpressionEvaluator());
        executionAttributes.put(
                StandardExpressions.STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME, getExpressionParser());
        executionAttributes.put(
                StandardExpressions.STANDARD_CONVERSION_SERVICE_ATTRIBUTE_NAME, getConversionService());

        return executionAttributes;

    }




    public IExpressionObjectsFactory getExpressionObjectsFactory() {
        return new StandardExpressionObjectsFactory();
    }





    /**
     * <p>
     *   Create a the set of Standard processors, all of them freshly instanced.
     * </p>
     *
     * @return the set of Standard processors.
     */
    private static Set<IProcessor> createStandardProcessorsSet() {
        /*
         * It is important that we create new instances here because, if there are
         * several dialects in the TemplateEngine that extend StandardDialect, they should
         * not be returning the exact same instances for their processors in order
         * to allow specific instances to be directly linked with their owner dialect.
         */
        final Set<IProcessor> processors = new LinkedHashSet<IProcessor>();
        processors.add(new StandardTextTagProcessor());
        processors.add(new StandardRemoveTagProcessor());
        processors.add(new StandardClassTagProcessor());
        processors.add(new StandardDefaultAttributesTagProcessor());
        processors.add(new StandardIncludeTagProcessor());
        processors.add(new StandardReplaceTagProcessor());
        processors.add(new StandardWithTagProcessor());
        processors.add(new StandardEachTagProcessor());
        processors.add(new StandardObjectTagProcessor());
        processors.add(new StandardInlineTagProcessor());
        processors.add(new StandardInliningTextProcessor());
        return processors;
    }


}