import java.util.*;

class CasinoSimulator {

    private static int totalGames = 0;
    private static int totalWins = 0;
    private static int totalLosses = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Добро пожаловать в Казино Симулятор!");

        System.out.print("Введите ваше имя: ");
        String playerName = scanner.nextLine();

        System.out.print("Введите ваш текущий банкролл: ");
        double bankroll = scanner.nextDouble();

        System.out.print("Введите вашу цель по выигрышу: ");
        double goal = scanner.nextDouble();

        boolean playerWins;

        System.out.println("\nВыберите тип азартной игры:");
        System.out.println("1. Блэкджек");
        //System.out.println("2. Покер");
        System.out.print("Введите номер выбранной игры: ");
        int gameChoice = scanner.nextInt();

        switch (gameChoice) {
            case 1:
                playerWins = playBlackjack(playerName, bankroll, goal);
                break;
            default:
                System.out.println("Выбрана недопустимая игра. Программа завершается.");
                return;
        }

        updateStatistics(playerWins);
    }
    private static final int[] CARD_VALUES = {2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 11};
    private static final String[] RANKS = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    private static final String[] SUITS = {"Hearts", "Diamonds", "Clubs", "Spades"};



    private static String getCardName(int card) {
        int rankIndex = card % 13;
        int suitIndex = card / 13;
        return RANKS[rankIndex] + " of " + SUITS[suitIndex];
    }





    private static void updateStatistics(boolean playerWins) {
        totalGames++;
        if (playerWins) {
            totalWins++;
        } else {
            totalLosses++;
        }


    }
    private static boolean playBlackjack(String playerName, double initialBankroll, double goal) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        double bankroll = initialBankroll;
        int numberOfGames = 0;
        boolean playerWins = false;

        System.out.println("\nДобро пожаловать в Блэкджек, " + playerName + "!");
        System.out.println("Ваш банкролл: " + bankroll);

        while (bankroll > 0 && bankroll < goal) {
            System.out.println("\nИгра " + (numberOfGames + 1) + ":");
            System.out.print("Введите вашу ставку: ");
            double betAmount = scanner.nextDouble();

            if (betAmount > bankroll) {
                System.out.println("Ставка превышает ваш банкролл. Введите меньшую сумму.");
                continue;
            }

            playerWins = simulateBlackjackRound(scanner);

            if (playerWins) {
                bankroll += betAmount;
                System.out.println("Вы выиграли раунд! Ваш банкролл: " + bankroll);
            } else {
                bankroll -= betAmount;
                System.out.println("Вы проиграли раунд. Ваш банкролл: " + bankroll);
            }

            numberOfGames++;

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        updateStatistics(playerWins);

        System.out.println("\nБлэкджек завершен. Ваш итоговый банкролл: " + bankroll);
        System.out.println("\nИтоговая статистика:");
        System.out.println("Общее количество игр: " + totalGames);
        System.out.println("Выигрыши: " + totalWins);
        System.out.println("Проигрыши: " + totalLosses);
        return playerWins;
    }
    private static boolean simulateBlackjackRound(Scanner scanner) {
        int[] playerHand = {drawCard(), drawCard()};
        int[] dealerHand = {drawCard(), drawCard()};

        System.out.println("Ваши карты: " + getCardNames(playerHand, SUITS));
        System.out.println("Карты дилера: " + getCardName(dealerHand[0], SUITS) + " и [Hidden]");



        if (isBlackjack(playerHand)) {
            System.out.println("У вас блэкджек!");
            return true;
        }

        while (true) {
            System.out.println("y-да, n-нет");
            System.out.print("Хотите взять еще карту? (y/n): ");
            char choice = scanner.next().charAt(0);

            if (choice == 'y') {
                int newCard = drawCard();
                playerHand = addCardToHand(playerHand, newCard);

                System.out.println("Вы получили карту: " + getCardName(newCard, SUITS));
                System.out.println("Ваши карты: " + getCardNames(playerHand, SUITS));

                if (getHandValue(playerHand) > 21) {
                    System.out.println("Вы проиграли, перебор!");
                    return false;
                }
            } else if (choice == 'n') {
                break;
            } else {
                System.out.println("Некорректный ввод. Попробуйте снова.");
            }
        }

        System.out.println("Открываем карты дилера: " + getCardNames(dealerHand, SUITS));

        while (getHandValue(dealerHand) < 17) {
            int newCard = drawCard();
            dealerHand = addCardToHand(dealerHand, newCard);
            System.out.println("Дилер взял карту: " + getCardName(newCard, SUITS));
        }

        System.out.println("Итоговые карты дилера: " + getCardNames(dealerHand, SUITS));

        if (getHandValue(dealerHand) > 21) {
            System.out.println("Дилер проиграл, перебор!");
            return true;
        }

        return getHandValue(playerHand) > getHandValue(dealerHand);
    }

    private static String getCardNames(int[] hand, String[] suits) {
        String[] cardNames = new String[hand.length];
        for (int i = 0; i < hand.length; i++) {
            cardNames[i] = getCardName(hand[i], suits);
        }
        return Arrays.toString(cardNames);
    }

    private static String getCardName(int card, String[] suits) {
        int rankIndex = card % 13;
        int suitIndex = card / 13;
        return RANKS[rankIndex] + " of " + suits[suitIndex];
    }



    private static String[] getCardNames(int[] hand) {
        String[] cardNames = new String[hand.length];
        for (int i = 0; i < hand.length; i++) {
            cardNames[i] = getCardName(hand[i]);
        }
        return cardNames;
    }

    private static int drawCard() {
        Random random = new Random();
        return random.nextInt(CARD_VALUES.length);
    }

    private static int[] addCardToHand(int[] hand, int card) {
        int[] newHand = Arrays.copyOf(hand, hand.length + 1);
        newHand[newHand.length - 1] = card;
        return newHand;
    }

    private static int getHandValue(int[] hand) {
        int sum = 0;
        int numAces = 0;

        for (int card : hand) {
            sum += CARD_VALUES[card];
            if (card == 12) {  // 12 соответствует тузу в массиве CARD_VALUES
                numAces++;
            }
        }

        while (sum > 21 && numAces > 0) {
            sum -= 10;
            numAces--;
        }

        return sum;
    }


    private static boolean isBlackjack(int[] hand) {
        return hand.length == 2 && getHandValue(hand) == 21;
    }



}