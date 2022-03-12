package com.dntech.demo.code.generator;

import com.squareup.javapoet.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OrderManagementServiceGenerator {

    private static String outputPackage;
    private static String outputPackagePath;

    public static void main(String[] args) throws IOException, ParseException {
        if (args.length < 2) {
            System.out.println("Wrong number of arguments");
            System.exit(1);
        }

        JSONParser parser = new JSONParser();
        JSONObject spec  = (JSONObject) parser.parse(new FileReader(args[0]));
        String outputPath =  args[1];

        JSONArray orderTypes = (JSONArray) spec.get("orderType");
        outputPackage = (String) spec.get("package");
        outputPackagePath = getOrderFullOutputPath(outputPath, outputPackage);
        generateBaseOrder();
        for (int i=0 ; i<orderTypes.size(); i++) {
            JSONObject orderType = (JSONObject) orderTypes.get(i);
            String dtoName = (String) orderType.get("dto");
            String orderState = (String) orderType.get("orderState");
            generateOrder(dtoName, orderState);
        }

    }

    private static String getOrderFullOutputPath(String outputPath, String outputPackage) {
        StringBuilder fullOutputPath = new StringBuilder(outputPath);
        fullOutputPath.append("\\\\");
        fullOutputPath.append(outputPackage.replaceAll("\\.", "\\\\\\\\"));
        return fullOutputPath.toString();
    }

    private static void generateOrder(String dtoName, String orderState) throws IOException {
        ClassName orderBaseClass = ClassName.get(outputPackage, "BaseOrder");
        TypeSpec.Builder builder =TypeSpec.classBuilder(dtoName)
                .addModifiers(Modifier.PUBLIC)
                .superclass(orderBaseClass);

        builder.addField(FieldSpec.builder(String.class,
                "orderState",
                Modifier.FINAL,
                Modifier.PRIVATE)
                .initializer("$S", orderState)
                .build());

        builder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class,"orderId")
                .addCode("super(orderId);")
                .build());

        builder.addMethod(MethodSpec.methodBuilder("getOrderState")
                .addModifiers(Modifier.PUBLIC)
                .addCode("\t return orderState ;")
                .returns(String.class)
                .build());

        writeToFile(builder, dtoName, outputPackagePath);
    }



    private static void generateBaseOrder() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder("BaseOrder")
                .addModifiers(Modifier.PUBLIC)
                .addField(FieldSpec.builder(String.class, "orderId", Modifier.FINAL, Modifier.PROTECTED).build());

        builder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PROTECTED)
                .addParameter(String.class,"orderId")
                .addCode("this.orderId=orderId" + ";")
                .build());

        builder.addMethod(MethodSpec.methodBuilder("getOrderId")
                .addModifiers(Modifier.PUBLIC)
                .addCode("\t return orderId;")
                .returns(String.class)
                .build());
        writeToFile(builder, "BaseOrder", outputPackagePath);
    }

    private static void writeToFile(TypeSpec.Builder builder, String className, String outputPath) throws IOException {
        JavaFile javaFile = JavaFile.builder(outputPackage, builder.build())
                .skipJavaLangImports(true)
                .build();
        new File(outputPath).mkdirs();
        File destFilename = new File(outputPath, className + ".java");
        Path path = Paths.get(destFilename.getPath());
        Writer writer = Files.newBufferedWriter(path, Charset.defaultCharset());
        javaFile.writeTo(writer);
        writer.close();
    }
}
