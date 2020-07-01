import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;

/**
 * 椭圆曲线数字签名算法
 */
public class ECDSA {
    // 该系统的椭圆曲线
    private ECC ecc;
    // 基点
    private final BigInteger[] g;
    // 基点的阶
    private final BigInteger n;

    /**
     * 构造一个签名系统
     *
     * @param q 椭圆曲线参数q
     * @param a 椭圆曲线参数a
     * @param b 椭圆曲线参数b
     * @param g 基点G
     * @param n 基点G的阶（要求是素数）
     */
    public ECDSA(BigInteger q, BigInteger a, BigInteger b, BigInteger[] g, BigInteger n) {
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
     * @return 公钥Q的横纵坐标，私钥d
     */
    private BigInteger[] generateKey() {
        SecureRandom secureRandom = new SecureRandom();
        BigInteger d;
        do {
            // 1<=d<=n-1
            d = new BigInteger(n.bitLength(), secureRandom);
        } while (d.equals(BigInteger.ZERO) || d.compareTo(n) >= 0);
        // Q=dG
        BigInteger[] q = ecc.multiply(d, g);
        return new BigInteger[] { q[0], q[1], d };
    }

    /**
     * 签名
     *
     * @param m 消息
     * @param d 公钥
     * @return 签名r, s
     */
    public BigInteger[] sign(BigInteger m, BigInteger d) {
        SecureRandom secureRandom = new SecureRandom();
        BigInteger r = BigInteger.ZERO;
        BigInteger s = BigInteger.ZERO;
        BigInteger k, e;
        BigInteger[] p;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA");
            while (true) {
                k = new BigInteger(n.bitLength(), secureRandom);
                // 1<=k<=n-1
                if (k.equals(BigInteger.ZERO) || k.compareTo(n) >= 0)
                    continue;
                // P=kG
                p = ecc.multiply(k, g);
                // r=xp % n
                r = p[0].mod(n);
                // 如果r=0，重新开始
                if (r.equals(BigInteger.ZERO))
                    continue;
                messageDigest.update(toByteArray(m));
                // e=Hash(m)
                e = new BigInteger(1, messageDigest.digest());
                // s=k^(-1)*(e+dr) % n
                s = k.modInverse(n).multiply(e.add(d.multiply(r))).mod(n);
                // 如果s=0，重新开始
                if (!s.equals(BigInteger.ZERO))
                    break;
            }
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return new BigInteger[] { r, s };
    }

    /**
     * 验证签名
     *
     * @param m 消息
     * @param r 签名r
     * @param s 签名s
     * @param q 私钥Q
     * @return 签名是否合法
     */
    public boolean verify(BigInteger m, BigInteger r, BigInteger s, BigInteger[] q) {
        // 判断1<=r, s<=n-1
        if (r.compareTo(BigInteger.ONE) < 0 || r.compareTo(n) >= 0 || s.compareTo(BigInteger.ONE) < 0
                || s.compareTo(n) >= 0)
            return false;
        BigInteger e = BigInteger.ONE;
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA");
            messageDigest.update(toByteArray(m));
            // e=Hash(m)
            e = new BigInteger(1, messageDigest.digest());
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        // w=s^(-1) mod n
        BigInteger w = s.modInverse(n);
        // u1=ew
        BigInteger u1 = e.multiply(w);
        // u2=rw
        BigInteger u2 = r.multiply(w);
        // X=u1*G+u2*Q
        BigInteger[] x = ecc.add(ecc.multiply(u1, g), ecc.multiply(u2, q));
        // 判断X!=O
        if (ECC.isZero(x))
            return false;
        // v=x_x % n
        BigInteger v = x[0].mod(n);
        // 判断v=r
        return v.equals(r);
    }

    /**
     * BigInteger转byte[]，去掉符号位0
     */
    private static byte[] toByteArray(BigInteger n) {
        byte[] b = n.toByteArray();
        if (b[0] == 0) {
            byte[] result = new byte[b.length - 1];
            System.arraycopy(b, 1, result, 0, result.length);
            return result;
        } else
            return b;
    }

    public static void main(String[] args) {
        // BigInteger q1 = new BigInteger("257");
        // BigInteger a = new BigInteger("0");
        // BigInteger b = new BigInteger("-4");
        // BigInteger[] g = new BigInteger[2];
        // g[0] = new BigInteger("126");
        // g[1] = new BigInteger("107");
        // BigInteger n = new BigInteger("43");
        // ECDSA ds = new ECDSA(q1, a, b, g, n);
        // BigInteger m = new BigInteger("12345");
        // BigInteger[] key = ds.generateKey();
        // BigInteger[] q = {key[0], key[1]};
        // BigInteger[] s = ds.sign(m, key[2]);
        // System.out.print(ds.verify(m, s[0], s[1], q));
        Scanner scanner = new Scanner(System.in);
        System.out.println("1. sign\n2. verify");
        int op = scanner.nextInt();
        System.out.print("q: ");
        BigInteger modQ = new BigInteger(scanner.next());
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
        ECDSA ecdsa = new ECDSA(modQ, a, b, g, n);
        System.out.print("m: ");
        BigInteger m = new BigInteger(scanner.next());
        if (op == 1) {
            BigInteger[] key = ecdsa.generateKey();
            System.out
                    .println("Q[0]: " + key[0].toString() + "\nQ[1]: " + key[1].toString() + "\nd: " + key[2].toString());
            BigInteger[] s = ecdsa.sign(m, key[2]);
            System.out.print("r: " + s[0].toString() + "\ns: " + s[1].toString());
        } else {
            System.out.print("r: ");
            BigInteger r = new BigInteger(scanner.next());
            System.out.print("s: ");
            BigInteger s = new BigInteger(scanner.next());
            BigInteger[] q = new BigInteger[2];
            System.out.print("Q[0]: ");
            q[0] = new BigInteger(scanner.next());
            System.out.print("Q[1]: ");
            q[1] = new BigInteger(scanner.next());
            System.out.print(ecdsa.verify(m, r, s, q));
        }
        scanner.close();
    }
}