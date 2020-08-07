package BeforeSend;

import DB.DBconnectVPS;
import LogsParts.LogsId;
import LogsParts.LogsT;
import sms.Sms;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckSymbolsSms {

    private static final Pattern PATTERN_CIRILLIC = Pattern.compile(".*[а-яА-ЯёЁ]{1,}.*");
    private static String regexpUCS2 = new StringBuilder().append("^[@£$¥èéùìòÇØø").append("ÅåΔ_ΦΓΛΩΠΨΣΘΞÆæßÉ ").append("\"").append("\\!\\#\\¤\\%\\&\'\\(\\)\\*\\+\\,\\-\\.\\/")
            .append("\\s\\d\\:\\;\\<\\=\\>\\?\\¡A-ZÄÖÑÜ\\§").append("¿a-zäöñüà\\^\\{\\}\\[\\~\\]\\|\\€]*$").toString();
    private static final Pattern PATTERN_UCS2 = Pattern.compile(regexpUCS2);
    private static Number logId;

    public static void check(Sms sms) throws SQLException {
        logId=sms.getId();
        String text = sms.getText();
        String textOneString = replaceAll("\n"," ",text);
        int qntCharsInText = 0;
        try{
            qntCharsInText = text.length();
        } catch (NullPointerException e){
            System.err.println(LogsT.printDate()+ LogsId.id(logId) + "test in sms is empty");
        }
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "id: " + sms.getId());
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "uniqid: " + sms.getUniqid() + " text.length(): " + qntCharsInText);
        int total = sms.getTotal();
        int part = sms.getPart();
        long uniqid = sms.getUniqid();
        boolean matches = false;
        try {
            //Matcher matcher = PATTERN_CIRILLIC.matcher(textOneString);
            Matcher matcher = PATTERN_UCS2.matcher(text);
            matches = matcher.matches();
        } catch (NullPointerException e){
            e.toString();
        }
        //System.out.println("Kirillic TRUE, Latin FALSE: " + matches);
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "Kirillic FALSE, Latin TRUE: " + matches);
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "matches: " + matches);
        //System.out.println("!matches: " + !matches);
        //while (total==1 && part==1 && uniqid<1){

        // 26.07.2018 добавил везде return для выхода из метода по нахождении нужной ветки. Проблема была что завершалось все в конце статусом UNKNOWN
        if (total>1 & qntCharsInText>0) {           //блок для составных смсок
            if (matches) {          //latin
                if (uniqid < 1) {
                    uniqidIsEmpty(sms);
                    return;
                } else {
                    //if (qntCharsInText > getQntSymbols(part, true)) {
                    if (qntCharsInText > 154) {
                        incorrectPartsLatinMessage(sms, uniqid, part, qntCharsInText);
                        return;
                    }
                    if (qntCharsInText <= 154 && checkRejectInOtherPartCompossiteMsg(sms, uniqid)){
                        correctSymbolsInMessage(sms);
                        //Bonding.bonding(sms.getUniqid());
                        return;
                    }
                }
            } else {                //kirrilic
                if (uniqid < 1) {
                    uniqidIsEmpty(sms);
                    return;
                } else {
                    //if (qntCharsInText > getQntSymbols(part, false)) {
                    if (qntCharsInText > 67) {
                        incorrectPartsKirillicMessage(sms, uniqid, part, qntCharsInText);
                        return;
                    }
                    if (qntCharsInText <= 67 && checkRejectInOtherPartCompossiteMsg(sms, uniqid)){
                        correctSymbolsInMessage(sms);
                        //Bonding.bonding(sms.getUniqid());
                        return;
                    }
                }
            }
        }
        if (total <= 1 & qntCharsInText>0) {        //if total==1
            if (matches) {          //latin
                if (uniqid < 1) {
                    //if (qntCharsInText > getQntSymbols(1, true)){
                    if (qntCharsInText > 160){
                        incorrectLatinSymbolsInMessage(sms, qntCharsInText);
                        return;
                    }
                    else {
                        correctSymbolsInSingleMessage(sms);
                        return;
                    }
                } else {
                    correctSymbolsInSingleMessage(sms);
                    return;
                }
            } else {
                if (uniqid < 1) {
                    //if (qntCharsInText > getQntSymbols(1, false)) {
                    if (qntCharsInText > 70) {
                        incorrectKirillicSymbolsInMessage(sms, qntCharsInText);
                        return;
                    }
                    else {
                        correctSymbolsInSingleMessage(sms);
                        return;
                    }
                } else {
                    correctSymbolsInSingleMessage(sms);
                    return;
                }
            }
        }
        if ( qntCharsInText==0){
            textIsNull(sms);
            return;
        }

        else unknownError(sms);

    }

    // true - russian    false - latin
    private static int getQntSymbols(int part, boolean lang){
        if (lang){//rus 70 134 201 268 335 402
            if(part==1)return 70;
            if(part==2)return 64;
            if(part>2)return 67;
        }
        else {//eng 160 306 459 612 765 918
            if(part==1)return 160;
            if(part==2)return 146;
            if(part>2)return 153;
        }
        return 0;
    }
// добавил в каждый метод sms.updateSmsToDB(); для того чтобы убрать в блоке с главной последовательностью дейтсвий void check(Sms sms)
    private static void incorrectKirillicSymbolsInMessage(Sms sms, int length) throws SQLException {
        logId=sms.getId();
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "incorrectKirillicSymbolsInMessage");
        sms.setAvailability("N");
        sms.setStatus("REJECTED");
        sms.setDescription(new StringBuilder().append("qnt symbols ").append(length).append(" > 70 for cirillic").toString());
        sms.updateSmsToDB();
    }

    private static void incorrectLatinSymbolsInMessage(Sms sms, int length) throws SQLException {
        logId=sms.getId();
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "incorrectLatinSymbolsInMessage");
        sms.setAvailability("N");
        sms.setStatus("REJECTED");
        sms.setDescription(new StringBuilder().append("qnt symbols ").append(length).append(" > 160 for latin").toString());
        sms.updateSmsToDB();
    }

    private static void correctSymbolsInMessage(Sms sms) throws SQLException {//в составном сообщении
        logId=sms.getId();
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "correctSymbolsInMessage");
        sms.setAvailability("N");
        sms.setStatus("ENROUTE");
        sms.setDescription("qnt symbols OK");
        sms.updateSmsToDB();
    }

    private static void correctSymbolsInSingleMessage(Sms sms) throws SQLException {
        logId=sms.getId();
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "correctSymbolsInSingleMessage");
        sms.setAvailability("N");
        sms.setStatus("ACCEPTED");
        sms.setDescription("WAIT time period");
        sms.updateSmsToDB();
    }

    private static void uniqidIsEmpty(Sms sms) throws SQLException {
        logId=sms.getId();
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "uniqidIsEmpty");
        sms.setAvailability("N");
        sms.setStatus("REJECTED");
        sms.setDescription("uniqid is empty");
        sms.updateSmsToDB();
    }

    private static void textIsNull(Sms sms) throws SQLException {
        logId=sms.getId();
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "textIsNull");
        sms.setAvailability("N");
        sms.setStatus("REJECTED");
        sms.setDescription("text is empty");
        sms.updateSmsToDB();
    }

    private static void unknownError(Sms sms) throws SQLException {
        logId=sms.getId();
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "unknownError");
        sms.setAvailability("N");
        sms.setStatus("UNKNOWN");
        sms.setDescription("unknown Error");
        sms.updateSmsToDB();
    }

    private static void incorrectPartsKirillicMessage(Sms sms, long uniqid, int part, int length) throws SQLException {
        logId=sms.getId();
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "incorrectPartsKirillicMessage()");
        String update = new StringBuilder().append("update smssystem.smslogs as ss SET ss.availability='N', ss.description=\"")
                .append(length).append(" symbols > 67 for kirillic in ")
                //.append(length).append(" symbols > ")
                //.append(getQntSymbols(part, true)).append(" for kirillic in ")
                .append(part).append(" part\", ss.status='REJECTED' where ss.uniqid=")
                .append(uniqid).toString();
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "incorrectPartsKirillicMessage str: " + update);
        DBconnectVPS.executeQuery(update);
//        DBconnectUpdate dbu = new DBconnectUpdate();
//        dbu.getStmt().execute(update);
        //dbu.closeConnection();
    }

    private static void incorrectPartsLatinMessage(Sms sms, long uniqid, int part, int length) throws SQLException {
        logId=sms.getId();
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "|incorrectPartsLatinMessage| uniqid:"+uniqid);
        String update = new StringBuilder().append("update smssystem.smslogs as ss SET ss.availability='N', ss.description=\"")
                .append(length).append(" symbols > 154 for latin in ")
//                .append(length).append(" symbols > ")
//                .append(getQntSymbols(part, false)).append(" for latin in ")
                .append(part).append(" part\", ss.status='REJECTED' where ss.uniqid=")
                .append(uniqid).toString();
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "|incorrectPartsLatinMessage| uniqid:"+uniqid+" str:"+update);
        DBconnectVPS.executeQuery(update);
//        DBconnectUpdate dbu = new DBconnectUpdate();
//        dbu.getStmt().execute(update);
        //dbu.closeConnection();
    }

    private static boolean checkRejectInOtherPartCompossiteMsg(Sms sms, long uniqid) throws SQLException {
        logId=sms.getId();
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "|checkRejectInOtherPartCompossiteMsg| uniqid:"+uniqid);
        String query = new StringBuilder(400).append("select * from smssystem.smslogs where status='REJECTED' and uniqid=").append(uniqid).toString();
        //DBconnectSelect db = new DBconnectSelect(query);
        int qntRows = DBconnectVPS.qntRowsInSelect(query);
        //if (db.qntRowsInSelect(db.getRs())>0){
        if (qntRows>0){
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "some parts in composite msg is regected! Return false");
            //db.closeConnectionWithRs();
            return false;
        }
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "|checkRejectInOtherPartCompossiteMsg| parts in composite msg is not rejected! Return true");
        //db.closeConnectionWithRs();
        return true;
    }

    private static String replaceAll(String regex, String replacement, String text) {
        return Pattern.compile(regex).matcher(text).replaceAll(replacement);
    }

}
