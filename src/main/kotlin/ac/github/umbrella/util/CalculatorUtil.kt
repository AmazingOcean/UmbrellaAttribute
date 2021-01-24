package ac.github.umbrella.util

import ac.github.umbrella.UmbrellaAttribute
import java.util.*
import java.util.regex.Pattern

class CalculatorUtil {

    companion object {
        var p = Pattern.compile("(?<!\\d)-?\\d+(\\.\\d+)?|[+\\-*/%()]") // 这个正则为匹配表达式中的数字或运算符


        private fun doubleCal(a1: Double, a2: Double, operator: Char): Double {
            when (operator) {
                '+' -> return a1 + a2
                '-' -> return a1 - a2
                '*' -> return a1 * a2
                '/' -> return a1 / a2
                '%' -> return a1 % a2
                else -> {
                }
            }
            throw Exception("illegal operator!")
        }

        private fun getPriority(s: String?): Int {
            if (s == null) {
                return 0
            }
            when (s) {
                "(" -> return 1
                "+", "-" -> return 2
                "*", "%", "/" -> return 3
                else -> {
                }
            }
            throw Exception("illegal operator!")
        }

        fun getResult(expr: String): Number {
            var expr = expr
            var intTransform = false
            if (expr.startsWith("int")) {
                intTransform = true
                expr = expr.substring(3)
            }
            /*数字栈*/
            val number = Stack<Double>()
            /*符号栈*/
            val operator = Stack<String?>()
            operator.push(null) // 在栈顶压人一个null，配合它的优先级，目的是减少下面程序的判断

            /* 将expr打散为运算数和运算符 */
            val m = p.matcher(expr)
            while (m.find()) {
                val temp = m.group()
                if (temp.matches("[+\\-*/%()]".toRegex())) { //遇到符号
                    if (temp == "(") { //遇到左括号，直接入符号栈
                        operator.push(temp)
                    } else if (temp == ")") { //遇到右括号，"符号栈弹栈取栈顶符号b，数字栈弹栈取栈顶数字a1，数字栈弹栈取栈顶数字a2，计算a2 b a1 ,将结果压入数字栈"，重复引号步骤至取栈顶为左括号，将左括号弹出
                        var b: String
                        while (operator.pop().also { b = it!! } != "(") {
                            val a1 = number.pop()
                            val a2 = number.pop()
                            number.push(doubleCal(a2, a1, b[0]))
                        }
                    } else { //遇到运算符，满足该运算符的优先级大于栈顶元素的优先级压栈；否则计算后压栈
                        while (getPriority(temp) <= getPriority(operator.peek())) {
                            val a1 = number.pop()
                            val a2 = number.pop()
                            val b = operator.pop()
                            number.push(doubleCal(a2, a1, b!![0]))
                        }
                        operator.push(temp)
                    }
                } else { //遇到数字，直接压入数字栈
                    number.push(java.lang.Double.valueOf(temp))
                }
            }
            while (operator.peek() != null) { //遍历结束后，符号栈数字栈依次弹栈计算，并将结果压入数字栈
                val a1 = number.pop()
                val a2 = number.pop()
                val b = operator.pop()
                number.push(doubleCal(a2, a1, b!![0]))
            }
            return if (intTransform) Math.round(number.pop()) else number.pop()
        }
    }

}