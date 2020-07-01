import java.math.BigInteger;

/**
 * 素数域椭圆曲线
 */
public class ECC {
    private final BigInteger p;
    private final BigInteger a;
    private final BigInteger b;

    public ECC(BigInteger p, BigInteger a, BigInteger b) {
        // (4a^3 + 27b^3) mod p != 0
        if (BigInteger.valueOf(4).multiply(a.modPow(BigInteger.valueOf(3), p))
                .add(BigInteger.valueOf(27).multiply(b.modPow(BigInteger.TWO, p))).mod(p).equals(BigInteger.ZERO))
            throw new IllegalArgumentException();
        this.p = p;
        this.a = a;
        this.b = b;
    }

    /**
     * 检查点c是否在曲线上
     *
     * @param c 点c的横纵坐标
     * @return 点c是否在曲线上
     */
    public boolean exist(BigInteger[] c) {
        return c[1].modPow(BigInteger.TWO, p)
                .equals(c[0].modPow(BigInteger.valueOf(3), p).add(a.multiply(c[0])).add(b).mod(p));
    }

    /**
     * 求负元
     */
    public BigInteger[] negate(BigInteger[] c) {
        return new BigInteger[] { c[0], c[1].negate().mod(p) };
    }

    /**
     * 椭圆曲线上点的加法
     *
     * @param pa 点a的横纵坐标
     * @param pb 点b的横纵坐标
     * @return 两点之和的坐标
     */
    public BigInteger[] add(BigInteger[] pa, BigInteger[] pb) {
        BigInteger k;
        // 点a为零点
        if (isZero(pa))
            return pb;
        // 点b为零点
        else if (isZero(pb))
            return pa;
        // 点a=点b
        else if (pa[0].equals(pb[0]) && pa[1].equals(pb[1])) {
            // 如果ya=yb=0，则a+b=o
            if (pa[1].equals(BigInteger.ZERO))
                return new BigInteger[] { BigInteger.ZERO, BigInteger.ZERO };
            // k=(3 * xa^2 + a)/(2ya) mod p
            k = BigInteger.valueOf(3).multiply(pa[0].modPow(BigInteger.TWO, p)).add(a)
                    .multiply(BigInteger.TWO.multiply(pa[1]).modInverse(p)).mod(p);
        }
        // 点a和点b互为负元
        else if (pa[0].equals(pb[0]))
            return new BigInteger[] { BigInteger.ZERO, BigInteger.ZERO };
        // 点a和点b不同
        else
            // k=(yb-ya)/(xb-xa) mod p
            k = pb[1].subtract(pa[1]).multiply(pb[0].subtract(pa[0]).modInverse(p)).mod(p);
        BigInteger[] pc = new BigInteger[2];
        // xc=k^2−xa−xb mod p
        pc[0] = k.modPow(BigInteger.TWO, p).subtract(pa[0]).subtract(pb[0]).mod(p);
        // yc=k(xa−xc​)−ya​
        pc[1] = k.multiply(pa[0].subtract(pc[0])).subtract(pa[1]).mod(p);
        return pc;
    }

    /**
     * 椭圆曲线上点的减法
     *
     * @param pa 点a的横纵坐标
     * @param pb 点b的横纵坐标
     * @return 两点之差的坐标
     */
    public BigInteger[] subtract(BigInteger[] pa, BigInteger[] pb) {
        return this.add(pa, this.negate(pb));
    }

    /**
     * 椭圆曲线上点的数乘(递归)
     *
     * @param k 倍数
     * @param c 点的坐标
     * @return 结果坐标
     */
    public BigInteger[] multiply(BigInteger k, BigInteger[] c) {
        if (k.compareTo(BigInteger.ZERO) <= 0)
            throw new IllegalArgumentException();
        // 如果k=1
        else if (k.equals(BigInteger.ONE))
            return c;
        // 如果k=2
        else if (k.equals(BigInteger.TWO))
            // 返回c + c
            return this.add(c, c);
        // 如果k>2
        else
        // 如果k为偶数
        if (k.mod(BigInteger.TWO).equals(BigInteger.ZERO))
            // 返回2 * (k/2 * c)
            return this.multiply(BigInteger.TWO, this.multiply(k.divide(BigInteger.TWO), c));
        // 如果k为奇数
        else
            // 返回(k-1)*c + c
            return this.add(this.multiply(k.subtract(BigInteger.ONE), c), c);

    }

    /**
     * 求点G的阶
     * 
     * @param g 点G的坐标
     * @return G的阶n
     */
    public BigInteger order(BigInteger[] g) {
        BigInteger n = BigInteger.ONE;
        BigInteger[] t = g;
        do {
            t = this.add(t, g);
            n = n.add(BigInteger.ONE);
        } while (!isZero(t));
        return n;
    }

    /**
     * 判断是否为零点
     */
    public static boolean isZero(BigInteger[] a) {
        return a[0].equals(BigInteger.ZERO) && a[1].equals(BigInteger.ZERO);
    }

    public static void main(String[] args) {
        BigInteger p1 = new BigInteger("257");
        BigInteger a = new BigInteger("0");
        BigInteger b = new BigInteger("-4");
        ECC ecc = new ECC(p1, a, b);
        BigInteger[] p = new BigInteger[2];
        p[0] = new BigInteger("2");
        p[1] = new BigInteger("7");
        BigInteger[] q = new BigInteger[2];
        q[0] = new BigInteger("34");
        q[1] = new BigInteger("39");
        BigInteger[] g = new BigInteger[2];
        g[0] = new BigInteger("126");
        g[1] = new BigInteger("107");
        BigInteger n = new BigInteger("19");
        BigInteger[] r = ecc.multiply(n, g);
        BigInteger[] x = new BigInteger[2];
        x[0] = new BigInteger("34");
        x[1] = new BigInteger("39");
        BigInteger[] y = new BigInteger[2];
        y[0] = new BigInteger("113");
        y[1] = new BigInteger("98");
        r = ecc.add(x, y);
        System.out.println(r[0].toString() + "\n" + r[1].toString());
    }
}