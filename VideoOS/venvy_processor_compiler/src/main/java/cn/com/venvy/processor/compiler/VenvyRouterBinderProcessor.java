package cn.com.venvy.processor.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import cn.com.venvy.processor.annotation.VenvyAutoData;
import cn.com.venvy.processor.annotation.VenvyRouter;

/**
 * Created by mingwei on 12/10/16.
 * CSDN:    http://blog.csdn.net/u013045971
 * Github:  https://github.com/gumingwei
 */
@AutoService(Processor.class)
public class VenvyRouterBinderProcessor extends AbstractProcessor {
    private Elements mElements;
    private Filer mFiler;
    private Messager mMessage;
    private HashMap<String, String> mCurrentAnnotationMap;
    private String mModuleName;

    private static final String BUILD_FILE_PACKAGE = "cn.com.venvy.processor.build";
    private static final String BUILD_FILE_NAME = "VenvyRoleMapUtil";


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(VenvyRouter.class.getCanonicalName());
        types.add(VenvyAutoData.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mElements = processingEnvironment.getElementUtils();
        mFiler = processingEnvironment.getFiler();
        mMessage = processingEnvironment.getMessager();
        if (mCurrentAnnotationMap == null) {
            mCurrentAnnotationMap = new HashMap<>();
        }
        Map<String, String> options = processingEnvironment.getOptions();
        if (options != null && options.size() > 0) {
            mModuleName = options.get("moduleName");
            if (null != mModuleName && !"".equals(mModuleName)) {
                mModuleName = mModuleName.replaceAll("[^0-9a-zA-Z_]+", "");
            }
        }
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotationMirror, ExecutableElement executableElement, String s) {
        return super.getCompletions(element, annotationMirror, executableElement, s);
    }

    @Override
    protected synchronized boolean isInitialized() {
        return super.isInitialized();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        try {
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(VenvyRouter.class);
            if (elements == null || elements.size() <= 0) {
                return true;
            }
            for (Element el : elements) {
                VenvyRouter router = el.getAnnotation(VenvyRouter.class);
                if (router == null || "null".equals(router.name()) || "null".equals(router.path())) {
                    continue;
                }
                String key;
                if ("".equals(router.path())) {
                    key = router.name();
                } else {
                    key = router.name() + "//" + router.path();
                }
                if (el.getKind() == ElementKind.CLASS) {
                    ProcessRoleCase routeRoleCase = new ProcessRoleCase();
                    routeRoleCase.className = ((TypeElement) el).getQualifiedName().toString();
                    routeRoleCase.type = router.type();
                    String value = routeRoleCase.toJson();
                    if (value != null) {
                        mCurrentAnnotationMap.put(key, value);
                    }
                }
            }
            if (null != mModuleName && !"".equals(mModuleName)) {
                createJavaFile(mCurrentAnnotationMap, mModuleName);
            }
        } catch (Exception e) {
            error("error and cause by: " + e.getMessage());
        }
        return true;
    }


    private void createJavaFile(HashMap<String, String> map, String fileName) throws Exception {


        ParameterizedTypeName inputMapTypeOfRoot = ParameterizedTypeName.get(
                ClassName.get(HashMap.class),
                ClassName.get(String.class),
                ClassName.get(String.class)
        );

        FieldSpec.Builder roleMapField = FieldSpec.builder(inputMapTypeOfRoot, "roleMap")
                .addModifiers(Modifier.FINAL, Modifier.PRIVATE, Modifier.STATIC)
                .initializer("new $T()", HashMap.class);


        MethodSpec.Builder getRoleMethod = MethodSpec.methodBuilder("findRole")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(TypeName.get(String.class))
                .addParameter(TypeName.get(String.class), "role")
                .addCode(CodeBlock.builder().add("return roleMap.get(role);").build());

        MethodSpec.Builder getAllRoleMethod = MethodSpec.methodBuilder("getAllRole")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(inputMapTypeOfRoot)
                .addCode(CodeBlock.builder().add("return roleMap;").build());

        CodeBlock.Builder codeBuild = CodeBlock.builder();
        if (map != null) {
            Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                codeBuild.addStatement("roleMap.put($S,$S)", entry.getKey(), entry.getValue());
            }
        }
        TypeSpec typeSpec = TypeSpec.classBuilder(fileName + "$$" + BUILD_FILE_NAME).addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get("cn.com.venvy.common.router", "IRouterFinder"))
                .addField(roleMapField.build())
                .addStaticBlock(codeBuild.build())
                .addMethod(getRoleMethod.build())
                .addMethod(getAllRoleMethod.build())
                .build();

        JavaFile.builder(BUILD_FILE_PACKAGE, typeSpec).build().writeTo(mFiler);
        mMessage.printMessage(Diagnostic.Kind.NOTE, "javafile had down");

    }

    private void error(String msg, Object... args) {
        mMessage.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    private void info(String msg, Object... args) {
        mMessage.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }
}