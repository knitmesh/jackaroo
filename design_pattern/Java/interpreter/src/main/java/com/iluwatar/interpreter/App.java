/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Seppälä
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.interpreter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Stack;

/**
 * 
 * The Interpreter pattern is a design pattern that specifies how to evaluate sentences in a
 * language. The basic idea is to have a class for each symbol (terminal or nonterminal) in a
 * specialized computer language. The syntax tree of a sentence in the language is an instance of
 * the composite pattern and is used to evaluate (interpret) the sentence for a client.
 * <p>
 * In this example we use the Interpreter pattern to break sentences into expressions (
 * {@link Expression}) that can be evaluated and as a whole form the result.
 * 
 */
public class App {

  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

  /**
   * 
   * Program entry point.
   * <p>
   * Expressions can be evaluated using prefix, infix or postfix notations This sample uses postfix,
   * where operator comes after the operands
   * 
   * @param args command line args
   * 
   */
  public static void main(String[] args) {
    String tokenString = "4 3 2 - 1 + *";
    Stack<Expression> stack = new Stack<>();

    String[] tokenList = tokenString.split(" ");
    for (String s : tokenList) {
      if (isOperator(s)) {
        Expression rightExpression = stack.pop();
        Expression leftExpression = stack.pop();
        LOGGER.info("popped from stack left: {} right: {}",
            leftExpression.interpret(), rightExpression.interpret());
        Expression operator = getOperatorInstance(s, leftExpression, rightExpression);
        LOGGER.info("operator: {}", operator);
        int result = operator.interpret();
        NumberExpression resultExpression = new NumberExpression(result);
        stack.push(resultExpression);
        LOGGER.info("push result to stack: {}", resultExpression.interpret());
      } else {
        Expression i = new NumberExpression(s);
        stack.push(i);
        LOGGER.info("push to stack: {}", i.interpret());
      }
    }
    LOGGER.info("result: {}", stack.pop().interpret());
  }

  public static boolean isOperator(String s) {
    return s.equals("+") || s.equals("-") || s.equals("*");
  }

  /**
   * Get expression for string
   */
  public static Expression getOperatorInstance(String s, Expression left, Expression right) {
    switch (s) {
      case "+":
        return new PlusExpression(left, right);
      case "-":
        return new MinusExpression(left, right);
      case "*":
        return new MultiplyExpression(left, right);
      default:
        return new MultiplyExpression(left, right);
    }
  }
}
