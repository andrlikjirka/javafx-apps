package utils;

//messenger predavajici zpravu pri chybnem zadani hodnoty (diky messengeru lze se zpravou nakladat v miste vzniku, obecna bunka nevi jestli je v ni hodnota displacement, ale s predanou zpravou se bude pracovat v miste, kde je jiste ze byla chybne zadana hodnota displacement)
public interface Messenger {
    public void sendMessage(String message);

    //konstanta s defaultni hodnotou messengeru
    Messenger DEFAULT_MESSANGER = new Messenger() {
        @Override
        public void sendMessage(String message) {
            System.err.println(message);
        }
    };
}
