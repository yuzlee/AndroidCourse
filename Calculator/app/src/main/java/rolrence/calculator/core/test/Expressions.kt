package rolrence.calculator.core.test

/**
 * Created by Rolrence on 9/11/2017.
 *
 */
class Expressions {
    companion object {
        val e1 = "5"
        val a1 = 5.0

        val e2 = "-123e1-34"
        val a2 =  -1264.0

        val e3 = "1+(-3+(2-2))"
        val a3 = -2.0

        val e4 = "-(30.22)*10-(12/2)"
        val a4 = -308.2

        val e5 = "8**2+(.2+32)-(-1+1)"
        val a5 = 96.2

        val e6 = "1+2*3"
        val a6 = 7.0

        //Functions and Constans
        //
        val e7 = "sin(pi/6)";
        val a7 =  0.5;

        val e8 = "cos(sqrt(pi*pi))+2"
        val a8 = 1.0
    }
}