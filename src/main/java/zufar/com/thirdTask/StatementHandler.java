package zufar.com.thirdTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class StatementHandler {

    private static Map<RuleKey, Double> rules = new HashMap<>();

    static Map<RuleKey, Double> createRulesByStatements(List<Statement> statements) {
        for (Statement statement : statements) {
            createRuleByStatement(statement);
        }
        return rules;
    }

    private static void createRuleByStatement(Statement statement) throws IllegalArgumentException {
        if (!isStatementCorrect(statement))
            throw new IllegalArgumentException("Error! Adding new statement is impossible! Statement is incorrect!");
        String firstUnit = statement.firstUnit;
        String secondUnit = statement.secondUnit;
        Double firstValue = statement.value;
        Double secondValue = 1 / firstValue;
        RuleKey firstRuleKey = new RuleKey(firstUnit, secondUnit);
        RuleKey secondRuleKey = new RuleKey(secondUnit, firstUnit);
        if (!isRuleKeyInRules(firstRuleKey)) {
            rules.put(firstRuleKey, firstValue);
        }
        if (!isRuleKeyInRules(secondRuleKey)) {
            rules.put(secondRuleKey, secondValue);
        }
        createNewRulesByStatement(statement);
    }

    private static boolean isStatementCorrect(Statement statement) {
        String firstValue = statement.firstUnit;
        String secondValue = statement.secondUnit;
        Double value = statement.value;
        return value == null || value != 0 || firstValue == null || !firstValue.isEmpty()
                || secondValue == null || !secondValue.isEmpty();
    }

    private static void createNewRulesByStatement(Statement statement) {
        Map<RuleKey, Double> newRules = new HashMap<>();
        for (Map.Entry<RuleKey, Double> current : rules.entrySet()) {
            String firstUnit1 = current.getKey().firstUnit;
            String firstUnit2 = statement.firstUnit;
            String secondUnit1 = current.getKey().secondUnit;
            String secondUnit2 = statement.secondUnit;
            if (isAbleToCreateNewRule(firstUnit1, firstUnit2, secondUnit1, secondUnit2)) {
                RuleKey firstRuleKey = new RuleKey(secondUnit1, secondUnit2);
                Double value1 = statement.value / current.getValue();
                if (!isRuleKeyInRules(firstRuleKey)) {
                    newRules.put(firstRuleKey, value1);
                }
                RuleKey secondRuleKey = new RuleKey(secondUnit2, secondUnit1);
                Double value2 = 1 / value1;
                if (!isRuleKeyInRules(secondRuleKey)) {
                    newRules.put(secondRuleKey, value2);
                }
            }
        }
        rules.putAll(newRules);
    }

    private static boolean isAbleToCreateNewRule(String firstUnit1, String firstUnit2, String secondUnit1, String secondUnit2) {
        return firstUnit1.equals(firstUnit2) && !secondUnit1.equals(secondUnit2);
    }

    static String handleStatement(Statement statement) {
        RuleKey key = new RuleKey(statement.firstUnit, statement.secondUnit);
        for (Map.Entry<RuleKey, Double> current : rules.entrySet()) {
            if (current.getKey().firstUnit.equals(key.firstUnit) && current.getKey().secondUnit.equals(key.secondUnit)) {
                Double neededValue = current.getValue();
                return key + " - " + neededValue;
            }
        }
        return "Conversion is impossible!";
    }

    static class RuleKey {
        final String firstUnit;
        final String secondUnit;

        RuleKey(String firstUnit, String secondUnit) {
            this.firstUnit = firstUnit;
            this.secondUnit = secondUnit;
        }

        @Override
        public String toString() {
            return firstUnit + "/" + secondUnit;
        }

        @Override
        public int hashCode() {
            return (firstUnit + secondUnit).intern().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final RuleKey other = (RuleKey) obj;

            if (this.hashCode() != other.hashCode()) {
                return false;
            }
            if (!this.firstUnit.equals(other.firstUnit)) {
                return false;
            }
            return this.secondUnit.equals(other.secondUnit);
        }
    }

    private static boolean isRuleKeyInRules(RuleKey pairKey) {
        return rules.containsKey(pairKey);
    }

    static void printRules() {
        if (rules.isEmpty()) {
            System.out.println("Rules are absent!");
            return;
        }
        System.out.println("All rules:");
        System.out.println("---------------------------------------");
        for (Map.Entry<StatementHandler.RuleKey, Double> current : rules.entrySet()) {
            System.out.println(current.getKey() + " - " + current.getValue());
        }
        System.out.println("---------------------------------------");
    }
}