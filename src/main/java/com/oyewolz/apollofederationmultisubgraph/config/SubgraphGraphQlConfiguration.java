package com.oyewolz.apollofederationmultisubgraph.config;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.graphql.data.federation.FederationSchemaFactory;
import org.springframework.graphql.data.method.annotation.support.AnnotatedControllerConfigurer;
import org.springframework.graphql.ExecutionGraphQlService;
import org.springframework.graphql.execution.DefaultExecutionGraphQlService;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.graphql.server.WebGraphQlHandler;
import org.springframework.graphql.server.webmvc.GraphQlHttpHandler;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration(proxyBeanMethods = false)
public class SubgraphGraphQlConfiguration {

    @Bean
    AnnotatedControllerConfigurer booksControllerConfigurer() {
        return controllerConfigurerFor(BooksSubgraph.class);
    }

    @Bean
    AnnotatedControllerConfigurer authorsControllerConfigurer() {
        return controllerConfigurerFor(AuthorsSubgraph.class);
    }

    @Bean
    FederationSchemaFactory booksFederationSchemaFactory() {
        return federationSchemaFactoryFor(BooksSubgraph.class);
    }

    @Bean
    FederationSchemaFactory authorsFederationSchemaFactory() {
        return federationSchemaFactoryFor(AuthorsSubgraph.class);
    }

    @Bean
    ExecutionGraphQlService booksExecutionGraphQlService(
            @Qualifier("booksControllerConfigurer") AnnotatedControllerConfigurer controllerConfigurer,
            @Qualifier("booksFederationSchemaFactory") FederationSchemaFactory federationSchemaFactory) {
        return new DefaultExecutionGraphQlService(
                graphQlSource("graphql/books.graphqls", controllerConfigurer, federationSchemaFactory));
    }

    @Bean
    ExecutionGraphQlService authorsExecutionGraphQlService(
            @Qualifier("authorsControllerConfigurer") AnnotatedControllerConfigurer controllerConfigurer,
            @Qualifier("authorsFederationSchemaFactory") FederationSchemaFactory federationSchemaFactory) {
        return new DefaultExecutionGraphQlService(
                graphQlSource("graphql/authors.graphqls", controllerConfigurer, federationSchemaFactory));
    }

    @Bean
    GraphQlHttpHandler booksGraphQlHttpHandler(
            @Qualifier("booksExecutionGraphQlService") ExecutionGraphQlService executionGraphQlService) {
        return new GraphQlHttpHandler(WebGraphQlHandler.builder(executionGraphQlService).build());
    }

    @Bean
    GraphQlHttpHandler authorsGraphQlHttpHandler(
            @Qualifier("authorsExecutionGraphQlService") ExecutionGraphQlService executionGraphQlService) {
        return new GraphQlHttpHandler(WebGraphQlHandler.builder(executionGraphQlService).build());
    }

    @Bean
    RouterFunction<ServerResponse> subgraphRoutes(
            @Qualifier("booksGraphQlHttpHandler") GraphQlHttpHandler booksHandler,
            @Qualifier("authorsGraphQlHttpHandler") GraphQlHttpHandler authorsHandler) {
        return RouterFunctions.route(RequestPredicates.POST("/graphql/books"), booksHandler::handleRequest)
                .andRoute(RequestPredicates.POST("/graphql/authors"), authorsHandler::handleRequest);
    }

    private AnnotatedControllerConfigurer controllerConfigurerFor(Class<?> markerAnnotation) {
        AnnotatedControllerConfigurer configurer = new AnnotatedControllerConfigurer();
        configurer.setControllerPredicate(controllerPredicate(asAnnotationType(markerAnnotation)));
        return configurer;
    }

    private FederationSchemaFactory federationSchemaFactoryFor(Class<?> markerAnnotation) {
        FederationSchemaFactory factory = new FederationSchemaFactory();
        factory.setControllerPredicate(controllerPredicate(asAnnotationType(markerAnnotation)));
        return factory;
    }

    private Predicate<Class<?>> controllerPredicate(Class<? extends Annotation> markerAnnotation) {
        return beanType -> AnnotatedElementUtils.hasAnnotation(beanType, markerAnnotation);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Annotation> asAnnotationType(Class<?> markerAnnotation) {
        return (Class<? extends Annotation>) markerAnnotation;
    }

    private GraphQlSource graphQlSource(
            String schemaPath,
            RuntimeWiringConfigurer controllerConfigurer,
            FederationSchemaFactory federationSchemaFactory) {
        Resource schemaResource = new ClassPathResource(schemaPath);
        return GraphQlSource.schemaResourceBuilder()
                .schemaResources(schemaResource)
                .configureRuntimeWiring(controllerConfigurer)
                .schemaFactory(federationSchemaFactory::createGraphQLSchema)
                .exceptionResolvers(List.of(((AnnotatedControllerConfigurer) controllerConfigurer).getExceptionResolver()))
                .build();
    }
}
