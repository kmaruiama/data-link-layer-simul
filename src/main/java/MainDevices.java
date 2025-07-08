import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MainDevices {
    public static void main(String[] args) throws Exception {
        byte[] macA = RandomizeMac.randomMac();
        byte[] macB = RandomizeMac.randomMac();
        byte[] macC = RandomizeMac.randomMac();

        byte[] broadcastMac = new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};

        Device deviceA = new Device(macA, 1024, "a");
        Device deviceB = new Device(macB, 1024, "b");
        Device deviceC = new Device(macC, 1024, "c");

        System.out.println("MAC A: " + macToString(macA));
        System.out.println("MAC B: " + macToString(macB));
        System.out.println("MAC C: " + macToString(macC));

        deviceA.connect("localhost", 9000);
        deviceB.connect("localhost", 9000);
        deviceC.connect("localhost", 9000);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                deviceA.send(
                        "Num buraco no chão vivia um hobbit. Não um buraco desagradável, sujo e úmido, cheio de pontas de minhocas e com cheiro de lodo; tampouco um buraco seco, vazio e arenoso, sem nada em que sentar ou o que comer: era um buraco de hobbit, e isso quer dizer conforto.\\n\n" +
                        "Tinha uma porta perfeitamente redonda, como um vigia, pintada de verde, com uma maçaneta de latão amarela e brilhante bem no meio. A porta abria-se para um pequeno hall tubular, parecido com um túnel: um túnel muito confortável, sem fumaça, com painéis de madeira e piso ladrilhado ou atapetado, com cadeiras envernizadas e muitos, muitos ganchos para casacos e chapéus — o hobbit era chegado a visitas.\\n\n" +
                        "O túnel prosseguia, serpenteando e depois subindo (mas não muito), até o fundo da colina — O Morro, como o povo do lugar o chamava. Em toda a parte havia pequenas portas redondas abrindo-se da mesma forma, primeiro de um lado e depois do outro. Nenhuma falta de organização: eram quartos, banheiros, porões, despensas (muitas despensas), guarda-roupas (um quarto inteiro para as roupas) e cozinhas (várias delas). O melhor de todos os quartos ficava do lado esquerdo (entrando), pois eram os únicos com janelas fundas e redondas que davam para o jardim e para os campos que desciam até o rio.\\n\n" +
                        "Este hobbit era um Bolseiro. O Bolseiro que vive no buraco. E a história toda é sobre como um Bolseiro teve uma aventura e se viu fazendo e dizendo coisas completamente inesperadas. Ele talvez tenha perdido o respeito dos vizinhos, mas ganhou... bem, espere para ver se ele ganhou alguma coisa no final.\\n\n" +
                        "A mãe de nosso hobbit — que é o que chamamos de Bilbo Bolseiro — era a famosa Belladonna Tûk, uma das três filhas do Velho Tûk, o chefe da família de hobbits que vivia através da Água, do outro lado do rio. Dizia-se que o Velho Tûk tinha uma mulher-fada (ou isso, ou uma aventura de verdade em sua juventude), e certamente os Tûks sempre foram um pouco menos respeitáveis e mais aventureiros do que os Bolseiros. De fato, havia Tûks que iam para as aventuras e secretamente não voltavam. Isso era absolutamente chocante para os Bolseiros, que se orgulhavam de jamais ter aventuras ou de fazer qualquer coisa inesperada. Você podia saber o que um Bolseiro faria sem precisar perguntar. Isso era muito, muito confortável.\\n\n" +
                        "Belladonna Tûk casou-se com o respeitável Bungo Bolseiro. Ele construiu para ela (e parte do seu dinheiro) o mais luxuoso buraco de hobbit que se podia encontrar no Morro, sob o Morro. E lá eles viveram felizes para sempre, e tiveram seu filho Bilbo. E então Belladonna morreu, e Bungo também, e Bilbo ficou com o buraco, e viveu nele, e permaneceu nele, e pensou que permaneceria para sempre. Tinha cerca de cinquenta anos, e era exatamente o que seus vizinhos esperavam dele: gordinho, com uma barriga que se expandia à medida que ele comia e bebia, e tinha bochechas vermelhas. Vestia coletes e jaquetas, e tinha o cabelo encaracolado. Seus pés eram grandes e peludos e usava sapatos. Era um hobbit muito respeitável.\\n\n" +
                        "Não tinha nenhum desejo de ter aventuras, nem de viajar, nem de subir montanhas. O seu dia ideal era comido no seu lugar, com uma boa refeição e uma chávena de chá. E depois, talvez, se houvesse tempo, outro chá. Adorava uma boa conversa sobre vegetais e uma tigela de sopa de abóbora. Sua vida era perfeita. E então, o que se podia fazer? Bem, o que se podia fazer, é que alguém o tinha visitado.\\n\n" +
                        "O sol estava forte e o verde estava verde. Bilbo estava sentado em seu banco do lado de fora da porta, fumando um cachimbo enorme de madeira, o maior que ele tinha, com fumaça que subia em anéis perfeitos e flutuava no ar. Era uma manhã agradável, quente e agradável, e Bilbo estava desfrutando-a. Não tinha nenhum pensamento na cabeça, exceto o de que talvez fosse hora de almoçar.\\n\n" +
                        "Então um velho apareceu. Ele tinha um chapéu azul pontudo, um longo manto cinzento, um cachecol prateado, e um cajado. Ele tinha uma barba longa, branca e que descia até o cinto. Seus olhos eram brilhantes e cintilantes. Bilbo o conhecia, mas não muito bem. Ele era um mago, um daqueles que aparecem ocasionalmente em Contos e Lendas, e que desaparecem tão rápido quanto aparecem. Seu nome era Gandalf.\\n\n" +
                        "Gandalf olhou para Bilbo de cima a baixo. \"Bom dia!\" disse Bilbo. Ele o fez de forma bastante alegre, pois o sol estava brilhando e a grama estava muito verde. \"O que você quer dizer com bom dia?\" disse Gandalf. \"Você quer dizer que é um bom dia, ou que deseja que eu tenha um bom dia, ou que você se sente bem neste dia, ou que é um dia para ser bom?\"\\n\n" +
                        "\"Tudo isso ao mesmo tempo!\" disse Bilbo. \"E um bom dia para um cachimbo, também, se você tiver um tabaco. Mas não tenho tempo para essas suas aventuras, nem hoje, nem nunca. Bom dia!\" Bilbo disse isso para se livrar do mago. Queria voltar para a sua casa e para o seu almoço. Mas Gandalf não se moveu.\\n\n" +
                        "\"É uma pena,\" disse Gandalf. \"Pois estou procurando alguém para ter uma aventura.\" Bilbo balançou a cabeça. \"Não estou interessado em aventuras. São coisas desagradáveis, que causam atrasos e perturbações. E te fazem perder o jantar. Não consigo entender o que se pode ver nelas.\"\\n\n" +
                        "\"Você é um Bolseiro, não é?\" disse Gandalf. \"O mesmo Bolseiro que morava aqui? Mas talvez eu esteja enganado. Talvez seja o neto do Velho Tûk. Ele sim era alguém que tinha aventuras.\"\\n\n" +
                        "\"Sou um Bolseiro\", disse Bilbo, bastante orgulhoso. \"E sou o filho de Bungo Bolseiro. E também o neto da famosa Belladonna Tûk, que tinha uma reputação! Mas não vejo necessidade de ter aventuras. Sou um hobbit muito respeitável.\"\\n\n" +
                        "\"É uma pena,\" disse Gandalf. \"É uma pena. Pois eu estou procurando alguém para ir em uma aventura. Alguém que tenha um pouco de... espírito.\"\\n\n" +
                        "\"Não tenho nenhum espírito,\" disse Bilbo, enfaticamente. \"Eu sou um Bolseiro. Não vou em aventuras.\"\\n\n" +
                        "Gandalf sorriu. \"Não tão rápido,\" disse ele. \"Vou passar por aqui de novo, na quarta-feira. Talvez você mude de ideia.\" E com um aceno, ele se virou e se afastou. Bilbo o observou ir, um pouco confuso. O que o mago queria dizer? E por que ele o tinha visitado?\\n\n" +
                        "Bilbo voltou para o seu buraco e para o seu almoço. Ele esqueceu Gandalf por um tempo. Mas então, na quarta-feira, a campainha tocou. Bilbo pensou que era apenas um vizinho, então abriu a porta sem olhar. Mas não era um vizinho. Era um anão.\\n\n" +
                        "O anão tinha uma barba branca e um capuz verde. Ele tinha um cinto de couro com uma fivela prateada e botas de couro. Ele tinha um machado de batalha pendurado na cintura e uma bolsa de ferramentas. Ele tinha um olhar severo e um sorriso.\\n\n" +
                        "\"Sou Dwalin,\" disse o anão. \"E vim para visitar o Bilbo Bolseiro.\" Bilbo ficou um pouco surpreso. Não esperava um anão. \"Claro, entre,\" disse Bilbo. \"Mas não sei por que você está aqui.\"\\n\n" +
                        "Dwalin entrou no hall e pendurou seu capuz em um dos ganchos de Bilbo. \"Fomos convidados,\" disse Dwalin. \"Para uma festa.\" Bilbo ficou ainda mais surpreso. Não se lembrava de ter convidado ninguém para uma festa. Ele não gostava de festas.\\n\n" +
                        "Então a campainha tocou de novo. Bilbo abriu a porta. Era outro anão. E depois outro. E depois mais um. E mais um. E mais um. Logo o hall de Bilbo estava cheio de anões. Treze anões, todos com suas barbas, seus capuzes, seus machados e suas bolsas de ferramentas.\\n\n" +
                        "Eles eram Dwalin e Balin, Kili e Fili, Dori, Nori e Ori, Oin e Gloin, Bifur, Bofur e Bombur, e, por último, mas não menos importante, Thorin Escudo de Carvalho, o chefe deles.\\n\n" +
                        "Bilbo ficou bastante nervoso. Nunca tinha tido tantos anões em sua casa antes. E eles estavam todos comendo a sua comida e bebendo a sua bebida. Ele não sabia o que fazer. \"O que vocês querem?\" perguntou Bilbo.\\n\n" +
                        "\"Queremos uma festa,\" disse Thorin. \"E uma aventura. E um tesouro.\" Bilbo quase desmaiou. Aventura? Tesouro? Ele não queria nenhuma dessas coisas. Ele só queria o seu almoço.\\n\n" +
                        "Mas os anões não se importavam. Eles estavam todos sentados à mesa de Bilbo, comendo e bebendo, e cantando canções. Bilbo tentou se livrar deles, mas eles não o deixavam. \"Não estou interessado em aventuras,\" disse Bilbo. \"Sou um hobbit muito respeitável.\"\\n\n" +
                        "\"É uma pena,\" disse Thorin. \"Pois estamos indo para a Montanha Solitária, para recuperar o nosso tesouro.\" Bilbo quase engasgou. A Montanha Solitária? Onde um dragão maligno chamado Smaug guardava um tesouro imenso?\\n\n" +
                        "\"Não vou,\" disse Bilbo. \"Não vou para a Montanha Solitária. É muito perigoso. E eu não sou um aventureiro.\"\\n\n" +
                        "\"É uma pena,\" disse Thorin. \"Pois vamos precisar de um ladrão. Um ladrão de verdade, que possa roubar o tesouro do dragão.\" Bilbo balançou a cabeça. Ele não era um ladrão. Ele era um hobbit respeitável.\\n\n" +
                        "Então Gandalf apareceu novamente. Ele tinha um sorriso no rosto e um brilho nos olhos. \"Você vai,\" disse Gandalf para Bilbo. \"Você vai para a Montanha Solitária, e vai ser o ladrão. E você vai gostar.\" Bilbo não acreditou nele.\\n\n" +
                        "Mas Gandalf não o deixou sair. Ele explicou aos anões que Bilbo era o melhor ladrão que eles poderiam encontrar. Bilbo não tinha certeza se isso era verdade. Ele nunca tinha roubado nada em sua vida.\\n\n" +
                        "No dia seguinte, Bilbo estava sentado em sua mesa, comendo o seu café da manhã. Ele estava pensando que os anões tinham ido embora. Mas então a campainha tocou. Era Gandalf e os anões.\\n\n" +
                        "\"Vamos,\" disse Gandalf. \"Está na hora de ir.\" Bilbo ficou um pouco assustado. Ele não queria ir. Mas Gandalf o pegou pela gola e o arrastou para fora da casa. E assim, Bilbo, o hobbit respeitável, foi para a sua aventura.\n" +
                        "```", macC);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(2000);
                deviceB.send("Mensagem Broadcast do B para todos", broadcastMac);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        Thread.sleep(15000);
    }

    private static String macToString(byte[] mac) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X", mac[i]));
            if (i < mac.length - 1) sb.append("-");
        }
        return sb.toString();
    }
}