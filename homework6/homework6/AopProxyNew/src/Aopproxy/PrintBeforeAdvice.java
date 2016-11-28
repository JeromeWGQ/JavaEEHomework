package Aopproxy;

import java.lang.reflect.Method;

public class PrintBeforeAdvice implements MethodBeforeAdvice {

    public PrintBeforeAdvice() {
    }

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("In PrintBeforeAdvice...");
    }
}
