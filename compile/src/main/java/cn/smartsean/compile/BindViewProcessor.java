package cn.smartsean.compile;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import cn.smartsean.annotation.BindView;

@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {

    /**
     * 代码写入工具
     */
    private Filer mFiler;
    /**
     * 日志打印
     */
    private Messager mMessager;
    /**
     * 跟元素相关的辅助类，帮助我们去获取一些元素相关的信息。
     */
    private Elements mElementUtils;

    private Map<String, ProxyInfo> mProxyInfoCacheMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessager = processingEnv.getMessager();
        mElementUtils = processingEnvironment.getElementUtils();
        mFiler = processingEnvironment.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new LinkedHashSet<>();
        annotationTypes.add(BindView.class.getCanonicalName());
        return annotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mProxyInfoCacheMap.clear();
        Set<? extends Element> elementsWithBindView = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element : elementsWithBindView) {
            checkAnnotationValid(element, BindView.class);

            // 获取成员变量元素
            VariableElement variableElement = (VariableElement) element;

            // 获取成员变量元素所在类元素
            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
            // 获取类的全称
            String fullClassName = classElement.getQualifiedName().toString();

            ProxyInfo proxyInfo  = mProxyInfoCacheMap.get(fullClassName);

            if (proxyInfo == null){
                proxyInfo = new ProxyInfo(mElementUtils,classElement);
                mProxyInfoCacheMap.put(fullClassName,proxyInfo);
            }

            BindView bindViewAnnotation = variableElement.getAnnotation(BindView.class);
            int id = bindViewAnnotation.value();
            proxyInfo.injectVariables.put(id,variableElement);
        }

        for (String s : mProxyInfoCacheMap.keySet()) {
            ProxyInfo proxyInfo = mProxyInfoCacheMap.get(s);
            try {
                JavaFileObject jfo = mFiler.createSourceFile(proxyInfo.getProxyClassFullName(),proxyInfo.getTypeElement());
                Writer writer = jfo.openWriter();
                writer.write(proxyInfo.generatedJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                error(proxyInfo.getTypeElement(),"Unable to write injector for type %s: %s",
                        proxyInfo.getTypeElement(),e.getMessage());
            }
        }
        return true;
    }

    /**
     * 检查注解是否可用
     *
     * @param annotatedElement
     * @param clazz
     * @return
     */
    private boolean checkAnnotationValid(Element annotatedElement, Class clazz) {
        if (annotatedElement.getKind() != ElementKind.FIELD) {
            error(annotatedElement, "%s must be declared on field.", clazz.getSimpleName());
            return false;
        }
        if (ClassUtils.isPrivate(annotatedElement)) {
            error(annotatedElement, "%s() must can not be private.", annotatedElement.getSimpleName());
            return false;
        }
        return true;
    }

    /**
     * 错误日志打印
     *
     * @param element
     * @param message
     * @param args
     */
    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        mMessager.printMessage(Diagnostic.Kind.NOTE, message, element);
    }


}
