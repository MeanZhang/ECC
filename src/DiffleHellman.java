import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Scanner;

public class DiffleHellman {
    // 该系统的椭圆曲线
    private final ECC ecc;
    // 基点的阶
    private final BigInteger n;
    // 基点
    private final BigInteger[] g;

    /**
     * 构造一个密钥交换系统
     *
     * @param q 椭圆曲线参数q
     * @param a 椭圆曲线参数a
     * @param b 椭圆曲线参数b
     * @param g 基点G
     * @param n 基点G的阶（要求是素数）
     */
    public DiffleHellman(BigInteger q, BigInteger a, BigInteger b, BigInteger[] g, BigInteger n) {
        ecc = new ECC(q, a, b);
        // G不在曲线上
        if (!ecc.exist(g))
            throw new IllegalArgumentException();
        // n不是素数
        if (!n.isProbablePrime((int) (n.bitLength() * 0.7)))
            throw new IllegalArgumentException();
        // n不是G的阶
        if (!ECC.isZero(ecc.multiply(n, g)))
            throw new IllegalArgumentException();
        this.g = g;
        this.n = n;
    }

    /**
     * 产生密钥
     * 
     * @return 私钥n，公钥P
     */
    public BigInteger[] generate() {
        SecureRandom secureRandom = new SecureRandom();
        BigInteger na;
        do {
            // 0 < na < n
            na = new BigInteger(n.bitLength(), secureRandom);
        } while (na.compareTo(n) < 0 && !na.equals(BigInteger.ONE));
        // Pa = na*G
        BigInteger[] pa = ecc.multiply(na, g);
        return new BigInteger[] { na, pa[0], pa[1] };
    }

    /**
     * 计算秘密钥
     * 
     * @param na 自己的私钥n
     * @param pb 另一方的公钥p
     * @return 秘密钥K
     */
    public BigInteger[] secretKey(BigInteger na, BigInteger[] pb) {
        // K=na*Pb
        return ecc.multiply(na, pb);
    }

    public static void main(String[] args) {
        // BigInteger q1 = new BigInteger("257");
        // BigInteger a = new BigInteger("0");
        // BigInteger b = new BigInteger("-4");
        // BigInteger[] g = new BigInteger[2];
        // g[0] = new BigInteger("126");
        // g[1] = new BigInteger("107");
        // BigInteger n = new BigInteger("43");
        // DiffleHellman dh = new DiffleHellman(q1, a, b, g, n);
        // BigInteger[] na = dh.generate();
        // BigInteger[] nb = dh.generate();
        // BigInteger[] pa = new BigInteger[] { na[1], na[2] };
        // BigInteger[] pb = new BigInteger[] { nb[1], nb[2] };
        // BigInteger[] ka = dh.secretKey(na[0], pb);
        // BigInteger[] kb = dh.secretKey(nb[0], pa);
        // System.out.println(ka[0].equals(kb[0]) && ka[1].equals(kb[1]));
        Scanner scanner = new Scanner(System.in);
        System.out.print("q: ");
        BigInteger q = new BigInteger(scanner.next());
        System.out.print("a: ");
        BigInteger a = new BigInteger(scanner.next());
        System.out.print("b: ");
        BigInteger b = new BigInteger(scanner.next());
        BigInteger[] g = new BigInteger[2];
        System.out.print("x_G: ");
        g[0] = new BigInteger(scanner.next());
        System.out.print("y_G: ");
        g[1] = new BigInteger(scanner.next());
        System.out.print("n: ");
        BigInteger n = new BigInteger(scanner.next());
        DiffleHellman dh = new DiffleHellman(q, a, b, g, n);
        BigInteger[] ka = dh.generate();
        BigInteger na = ka[0];
        BigInteger[] pa = new BigInteger[2];
        pa[0] = ka[1];
        pa[1] = ka[2];
        System.out.println("na: " + na.toString() + "\npa: (" + pa[0].toString() + ", " + pa[1].toString() + ")");
        BigInteger[] kb = dh.generate();
        BigInteger nb = kb[0];
        BigInteger[] pb = new BigInteger[2];
        pb[0] = kb[1];
        pb[1] = kb[2];
        System.out.println("nb: " + nb.toString() + "\npb: (" + pb[0].toString() + ", " + pb[1].toString() + ")");
        BigInteger[] k = dh.secretKey(na, pb);
        System.out.println("ka: (" + k[0].toString() + ", " + k[1].toString() + ")");
        k = dh.secretKey(nb, pa);
        System.out.println("kb: (" + k[0].toString() + ", " + k[1].toString() + ")");
        scanner.close();
    }
}