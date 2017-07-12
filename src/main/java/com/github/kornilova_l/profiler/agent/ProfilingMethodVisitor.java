package com.github.kornilova_l.profiler.agent;

import com.github.kornilova_l.config.MethodConfig;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ProfilingMethodVisitor extends AdviceAdapter {
    private final static Pattern allParamsPattern = Pattern.compile("(\\(.*\\))");
    private final static Pattern returnTypePattern = Pattern.compile("(?<=\\)).*"); // (?<=\)).*
    private final static String LOGGER_PACKAGE_NAME = "com/github/kornilova_l/profiler/logger/";
    private final String methodName;
    private final String className;
    private final boolean hasSystemCL;
    private final MethodConfig methodConfig;

    ProfilingMethodVisitor(int access, String methodName, String desc,
                           MethodVisitor mv, String className, boolean hasSystemCL, MethodConfig methodConfig) {
        super(Opcodes.ASM5, mv, access, methodName, desc);
        this.className = className;
        this.methodName = methodName;
        this.hasSystemCL = hasSystemCL;
        this.methodConfig = methodConfig;
    }

    private static int getSizeOfRetVal(int opcode) {
        if (opcode == LRETURN || // long
                opcode == DRETURN) { // double
            return 2;
        }
        return 1;
    }

    @Override
    protected void onMethodEnter() {
        getThreadId();
        getTime();
        getClassNameAndMethodName();
        mv.visitLdcInsn(methodDesc);
        getIsStatic();
        int countEnabledParams = (int) methodConfig.parameters.stream().filter((parameter -> parameter.isEnable)).count();
        if (countEnabledParams > 0) { // if at least one parameter is enabled
            getArrayWithParameters(countEnabledParams);
        } else {
            loadNull();
        }
        addToQueue(Type.Enter);
    }

    private void addToQueue(Type type) {
        String description = null;
        switch (type) {
            case Enter:
                description = "(JJLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Z[Ljava/lang/Object;)V";
                break;
            case Exit:
                description = "(Ljava/lang/Object;JJ)V";
                break;
            case Exception:
                description = "(Ljava/lang/Throwable;JJ)V";
                break;
        }
        if (hasSystemCL) {
            mv.visitMethodInsn(INVOKESTATIC, LOGGER_PACKAGE_NAME + "Logger", "addToQueue",
                    description, false);
        } else {
            mv.visitMethodInsn(INVOKESTATIC, LOGGER_PACKAGE_NAME + "Proxy", "addToQueue",
                    description, false);
        }
    }

    private void getArrayWithParameters(int arraySize) {
        createObjArray(arraySize);
        int posOfParam = 0;
        if (!isStatic()) {
            posOfParam = 1;
        }
        int index = 0;
        for (int i = 0; i < methodConfig.parameters.size(); i++) {
            MethodConfig.Parameter parameter = methodConfig.parameters.get(i);
            if (parameter.isEnable) {
                mv.visitInsn(DUP); // array reference
                getIConst(index++); // index of element
                paramToObj(parameter.getJvmType(), posOfParam);
                visitInsn(AASTORE); // load obj to array
            }
            posOfParam = getObjSize(parameter.getJvmType());
        }
    }

    private void loadNull() {
        mv.visitInsn(ACONST_NULL);
    }

    private void createObjArray(int arraySize) {
        getIConst(arraySize);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
    }

    private void loadThisToArr() {
        mv.visitInsn(DUP); // array reference
        getIConst(0); // index of element
        visitVarInsn(ALOAD, 0);
        visitInsn(AASTORE); // load obj to array
    }

    private void paramToObj(String paramDesc, int pos) {
        switch (paramDesc) {
            case "I": // int
                mv.visitVarInsn(ILOAD, pos);
                intToObj();
                break;
            case "J": // long
                mv.visitVarInsn(LLOAD, pos);
                longToObj();
                break;
            case "Z": // boolean
                mv.visitVarInsn(ILOAD, pos);
                booleanToObj();
                break;
            case "C": // char
                mv.visitVarInsn(ILOAD, pos);
                charToObj();
                break;
            case "S": // short
                mv.visitVarInsn(ILOAD, pos);
                shortToObj();
                break;
            case "B": // byte
                mv.visitVarInsn(ILOAD, pos);
                byteToObj();
                break;
            case "F": // float
                mv.visitVarInsn(FLOAD, pos);
                floatToObj();
                break;
            case "D": // double
                mv.visitVarInsn(DLOAD, pos);
                doubleToObj();
                break;
            default: // object
                mv.visitVarInsn(ALOAD, pos);
        }
    }

    private static int getObjSize(String paramDesc) {
        switch (paramDesc) {
            case "Z": // boolean
            case "I": // int
            case "C": // char
            case "S": // short
            case "B": // byte
            case "F": // float
                return 1;
            case "J": // long
            case "D": // double
                return 2;
            default: // object
                return 1;
        }
    }

    private void doubleToObj() {
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf",
                "(D)Ljava/lang/Double;", false);
    }

    private void floatToObj() {
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf",
                "(F)Ljava/lang/Float;", false);
    }

    private void byteToObj() {
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf",
                "(B)Ljava/lang/Byte;", false);
    }

    private void shortToObj() {
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf",
                "(S)Ljava/lang/Short;", false);
    }

    private void charToObj() {
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf",
                "(C)Ljava/lang/Character;", false);
    }

    private void booleanToObj() {
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf",
                "(Z)Ljava/lang/Boolean;", false);
    }

    private void longToObj() {
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf",
                "(J)Ljava/lang/Long;", false);
    }

    private void intToObj() {
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf",
                "(I)Ljava/lang/Integer;", false);
    }

    private void getIConst(int i) {
        if (i < 6) {
            mv.visitInsn(ICONST_0 + i);
        } else {
            mv.visitIntInsn(BIPUSH, i);
        }
    }

    private void getIsStatic() {
        if (isStatic()) {
            mv.visitInsn(ICONST_1);
        } else {
            mv.visitInsn(ICONST_0);
        }
    }

    private void getClassNameAndMethodName() {
        mv.visitLdcInsn(className);
        mv.visitLdcInsn(methodName);
    }

    private void getTime() {
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System",
                "currentTimeMillis", "()J", false);
    }

    private void getThreadId() {
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread",
                "()Ljava/lang/Thread;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getId", "()J", false);
    }

    private String getPartOfDescWithParam() {
        Matcher m = allParamsPattern.matcher(methodDesc);
        if (!m.find()) {
            throw new IllegalArgumentException("Method signature does not contain parameters");
        }
        return m.group(1);
    }


    private boolean isStatic() {
        return (methodAccess & ACC_STATIC) != 0;
    }

    @Override
    protected void onMethodExit(int opcode) {
        if (opcode == ATHROW) {
            dup();
            getThreadId();
            getTime();
            addToQueue(Type.Exception);
            return;
        }
        if (opcode != RETURN) { // return some value
            int sizeOfRetVal = getSizeOfRetVal(opcode);
            dupRetVal(sizeOfRetVal);
        }
        retValToObj();
        getThreadId();
        getTime();
        addToQueue(Type.Exit);
    }

    private void retValToObj() {
        Matcher m = returnTypePattern.matcher(methodDesc);
        if (!m.find()) {
            throw new IllegalArgumentException("Description does not have return value");
        }
        String retType = m.group();
        if (Objects.equals(retType, "V")) {
            loadNull();
        } else {
            convertToObj(retType);
        }
    }

    private void convertToObj(String type) {
        switch (type) {
            case "I": // int
                intToObj();
                break;
            case "J": // long
                longToObj();
                break;
            case "Z": // boolean
                booleanToObj();
                break;
            case "C": // char
                charToObj();
                break;
            case "S": // short
                shortToObj();
                break;
            case "B": // byte
                byteToObj();
                break;
            case "F": // float
                floatToObj();
                break;
            case "D": // double
                doubleToObj();
                break;
        }
    }

    private void dupRetVal(int sizeOfRetVal) {
        if (sizeOfRetVal == 1) {
            dup();
        } else {
            dup2();
        }

    }

    private enum Type {
        Exit,
        Enter,
        Exception
    }
}
