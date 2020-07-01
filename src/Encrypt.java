import java.math.BigInteger;
import java.util.Scanner;

/**
 * 素数域椭圆曲线简单加解密
 */
public class Encrypt {
    private final ECC ecc;
    private final BigInteger[] g;

    public Encrypt(BigInteger q, BigInteger a, BigInteger b, BigInteger[] g) {
        ecc = new ECC(q, a, b);
        if (!ecc.exist(g))
            throw new IllegalArgumentException("g does'n exist.");
        this.g = g;
    }

    /**
     * 椭圆曲线加密
     * 
     * @param m 消息
     * @param k 随机产生的整数
     * @param p 接收方公钥
     * @return 密文
     */
    public BigInteger[][] encrypt(BigInteger[] m, BigInteger k, BigInteger[] p) {
        if (!ecc.exist(m))
            throw new IllegalArgumentException("Message is illegal.");
        BigInteger[][] c = new BigInteger[2][2];
        // kg
        c[0] = ecc.multiply(k, g);
        // m+kp
        c[1] = ecc.add(m, ecc.multiply(k, p));
        return c;
    }

    /**
     * 解密
     * 
     * @param c 密文
     * @param n 私钥
     * @return 明文
     */
    public BigInteger[] decrypt(BigInteger[][] c, BigInteger n) {
        if (!ecc.exist(c[0]) || !ecc.exist(c[1]))
            throw new IllegalArgumentException("Cipher is illegal.");
        // m=c[1]-n*c[0]
        return ecc.subtract(c[1], ecc.multiply(n, c[0]));
    }

    public static void main(String[] args) {
        // BigInteger q=new BigInteger("257");
        // BigInteger a=new BigInteger("0");
        // BigInteger b=new BigInteger("-4");
        // BigInteger[] g=new BigInteger[]{BigInteger.valueOf(2),BigInteger.valueOf(2)};
        // BigInteger k = new BigInteger("41");
        // BigInteger[] p =new BigInteger[]{BigInteger.valueOf(197),BigInteger.valueOf(167)};
        // BigInteger n =new BigInteger("101");
        // BigInteger[] m =new BigInteger[]{BigInteger.valueOf(112),BigInteger.valueOf(26)};
        // EncryptDecrypt ed =new EncryptDecrypt(q, a, b, g);
        // BigInteger[][] c=ed.encrypt(m, k, p);
        // System.out.println(c[0][0].toString()+"
        // "+c[0][1].toString()+"\n"+c[1][0].toString()+" "+c[1][1].toString());
        // BigInteger[] m2=ed.decrypt(c, n);
        // System.out.println(m2[0].toString()+" "+m2[1].toString());
        Scanner scanner = new Scanner(System.in);
        System.out.println("1. encrypt\n2. decrypt");
        int op = scanner.nextInt();
        System.out.print("q: ");
        BigInteger q = new BigInteger(scanner.next());
        System.out.print("a: ");
        BigInteger a = new BigInteger(scanner.next());
        System.out.print("b: ");
        BigInteger b = new BigInteger(scanner.next());
        BigInteger[] g = new BigInteger[2];
        System.out.print("x_g: ");
        g[0] = new BigInteger(scanner.next());
        System.out.print("y_g: ");
        g[1] = new BigInteger(scanner.next());
        Encrypt ed = new Encrypt(q, a, b, g);
        if (op == 1) {
            BigInteger[] m = new BigInteger[2];
            System.out.print("x_m: ");
            m[0] = new BigInteger(scanner.next());
            System.out.print("y_m: ");
            m[1] = new BigInteger(scanner.next());
            System.out.print("k: ");
            BigInteger k = new BigInteger(scanner.next());
            BigInteger[] p = new BigInteger[2];
            System.out.print("x_p: ");
            p[0] = new BigInteger(scanner.next());
            System.out.print("y_p: ");
            p[1] = new BigInteger(scanner.next());
            BigInteger[][] c = ed.encrypt(m, k, p);
            System.out.println("c:");
            System.out.print("("+c[0][0].toString()+", "+c[0][1].toString()+")\n("
            +c[1][0].toString()+", "+c[1][1].toString()+")");
        }
        else{
            BigInteger[][] c=new BigInteger[2][2];
            System.out.print("c[0][0]: ");
            c[0][0] = new BigInteger(scanner.next());
            System.out.print("c[0][1]: ");
            c[0][1] = new BigInteger(scanner.next());
            System.out.print("c[1][0]: ");
            c[1][0] = new BigInteger(scanner.next());
            System.out.print("c[1][1]: ");
            c[1][1] = new BigInteger(scanner.next());
            System.out.print("n: ");
            BigInteger n = new BigInteger(scanner.next());
            BigInteger[] m=ed.decrypt(c, n);
            System.out.println("m:");
            System.out.print("("+m[0].toString()+", "+m[1].toString()+")");
        }
        scanner.close();
    }
}