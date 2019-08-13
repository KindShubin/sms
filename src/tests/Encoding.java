package tests;

import LogsParts.LogsT;
import org.mozilla.universalchardet.UniversalDetector;

public class Encoding {

        public static void main(String[] args) throws java.io.IOException
        {
            if (args.length != 1) {
                System.err.println(LogsT.printDate() + "Usage: java TestDetector FILENAME");
                System.exit(1);
            }

            byte[] buf = new byte[4096];
            String fileName = args[0];
            java.io.FileInputStream fis = new java.io.FileInputStream(fileName);

            // (1)
            UniversalDetector detector = new UniversalDetector(null);

            // (2)
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            // (3)
            detector.dataEnd();

            // (4)
            String encoding = detector.getDetectedCharset();
            if (encoding != null) {
                System.out.println(LogsT.printDate() + "Detected encoding = " + encoding);
            } else {
                System.out.println(LogsT.printDate() + "No encoding detected.");
            }

            // (5)
            detector.reset();
        }
}
