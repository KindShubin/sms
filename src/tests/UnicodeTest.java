package tests;

import java.util.regex.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

public class UnicodeTest {

    public static String replaceAll(String regex, String replacement, String text) {
        return Pattern.compile(regex).matcher(text).replaceAll(replacement);
    }

    public static void main(String[] args) {
        String regexp = new StringBuilder().append("^[@£$¥èéùìòÇØø").append("ÅåΔ_ΦΓΛΩΠΨΣΘΞÆæßÉ ").append("\\!\"\\#\\¤\\%\\&\'\\(\\)\\*\\+\\,\\-\\.\\/")
                .append("\\s\\d\\:\\;\\<\\=\\>\\?\\¡A-ZÄÖÑÜ\\§").append("¿a-zäöñüà\\^\\{\\}\\[\\~\\]\\|\\€]*$").toString();
        System.out.println("regexp: "+regexp);
        Pattern PATTERN_CIRILLIC = Pattern.compile(regexp);
        String  ololo = "^[@]$";
        //Pattern PATTERN_UCS = Pattern.compile("^[@£\\$\\¥èéùìòÇØøÅåΔ\\_ΦΓΛΩΠΨΣΘΞÆæßÉ \\!\"\\#\\¤\\%\\&\'\\(\\)\\*\\+\\,\\-\\.\\/\\s\\d\\:\\;\\<\\=\\>\\?\\¡A-ZÄÖÑÜ\\§\\¿a-zäöñüà\\^\\{\\}\\\\[\\~\\]\\|\\€\\]*$");
        //'/^[\@£\$¥èéùìòÇ\nØø\rÅåΔ_ΦΓΛΩΠΨΣΘΞ\x1BÆæßÉ !\"#¤%&\'\(\)\*\+,\-\.\/\d\:;<\=>\?¡A-ZÄÖÑÜ§¿a-zäöñüà\x0C\^\{\}\\\[~\]\|€]*$/u'
        //Pattern PATTERN_CIRILLIC = Pattern.compile(".*[а-яА-ЯёЁ]{1,}.*");
        //Pattern PATTERN_CIRILLIC = Pattern.compile("/[а-яА-Я]/gx");
        String str3 = "i don't know english\n" + "very ΠΨΣ \"+-*:;,. sйad";
        String str4 = "الرمز هو: \u200F9299الرمز هو: \u200F92991\u200F. انتقل إلى فيسبوك وأد للتأكيد.  #fb71";
        String text = "[10:19:19] [Dev team] Valeriуфy Mari\n" +
                "nets: Доброго утра 1\n" +
                "[De00000 1233ds";
        String tesxt1 = replaceAll("\n"," ",str4);
        System.out.println("text1: "+tesxt1);
//        boolean matches = false;
        Matcher matcher = PATTERN_CIRILLIC.matcher(tesxt1);
        boolean matches = matcher.matches();
        //System.out.println(matcher.toString());
        //System.out.println(matcher.toMatchResult());
        //System.out.println(matches);
        System.out.println(matcher.find() ?
                "I found '"+matcher.group()+"' starting at index "+matcher.start()+" and ending at index "+matcher.end()+"." :
                "I found nothing!");
        System.out.println("Kirillic TRUE, Latin FALSE: " + matches);
        System.out.println("matches: " + matches);
        System.out.println("!matches: " + !matches);

        String bond = "Текст \"что-то в кавычках\" потом какой-то текст и одна кавычка\"";
        System.out.println("base: "+bond);
        String bondingText = bond.replace ("\"", "\\\"");
        System.out.println("result: "+bondingText);
//
//        String str1 = "12243fdvdgb DGER 43 $&#$!";
//        String str2 = "12243fdvпdgb DGER 43 $&#$!";
//        //String str3 = "Лталытпды ыдаьыдл437 длва №;;Г*";
//        Pattern pattern = Pattern.compile(".*[а-яА-Я]+.*");
//        Matcher matcher1 = pattern.matcher(str1);
//        Matcher matcher2 = pattern.matcher(str2);
//        Matcher matcher3 = pattern.matcher(str3);
//        System.out.println("str1: "+ matcher1.matches());
//        System.out.println("str2: "+ matcher2.matches());
//        System.out.println("str3: "+ matcher3.matches());
//
//        System.out.println(Character.getType('ф'));
//        System.out.println(Character.getNumericValue('ф'));
//        System.out.println(Character.hashCode('f'));
//        System.out.println(Character.isUnicodeIdentifierPart('ф'));
//        System.out.println(Character.isUnicodeIdentifierStart('ф'));
//        //System.out.println(UnicodeFormatter.byteToHex(
//        char[] charArr = {'a','b','c','й','ц','у','1','2','3'};
//        System.out.println(Integer.toHexString(15));
//        String str = "abcйцу123";
//        System.out.println("char Array:");
//        System.out.printf("a - %s, b - %s, c - %s, й - %s, ц - %s, у - %s, 1 - %s, 2 - %s, 3 - %s,"
////                ,Integer.toHexString(codePointAt(charArr, 0))
////                ,Integer.toHexString(codePointAt(charArr, 1))
////                ,Integer.toHexString(codePointAt(charArr, 2))
////                ,Integer.toHexString(codePointAt(charArr, 3))
////                ,Integer.toHexString(codePointAt(charArr, 4))
////                ,Integer.toHexString(codePointAt(charArr, 5))
////                ,Integer.toHexString(codePointAt(charArr, 6))
////                ,Integer.toHexString(codePointAt(charArr, 7))
////                ,Integer.toHexString(codePointAt(charArr, 8)));
////                ,convert(codePointAt(charArr,0))
////                ,convert(codePointAt(charArr,1))
////                ,convert(codePointAt(charArr,2))
////                ,convert(codePointAt(charArr,3))
////                ,convert(codePointAt(charArr,4))
////                ,convert(codePointAt(charArr,5))
////                ,convert(codePointAt(charArr,6))
////                ,convert(codePointAt(charArr, 7))
////                ,convert(codePointAt(charArr,8)));
//                ,codePointAt(charArr,0)
//                ,codePointAt(charArr,1)
//                ,codePointAt(charArr,2)
//                ,codePointAt(charArr,3)
//                ,codePointAt(charArr,4)
//                ,codePointAt(charArr,5)
//                ,codePointAt(charArr,6)
//                ,codePointAt(charArr, 7)
//                ,codePointAt(charArr,8));
//    }
//    public static int convert(int n) {
//        return Integer.valueOf(String.valueOf(n), 16);
    }
}
