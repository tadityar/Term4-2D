package sat;

import immutable.EmptyImList;
import immutable.ImList;
import immutable.NonEmptyImList;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.NegLiteral;
import sat.formula.PosLiteral;

import java.io.*;

/**
 * A DIMACS parser to feed the SATSolver.
 */
public class DIMACSParser {

    ImList<Clause> populatedImList;


    /**
     * This method must be called after calling getClauseList() so that populatedImList has been populated.
     *
     * @param cnf_inp
     * @return Formula needed by the SAT Solver
     */
    public Formula getFormula(File cnf_inp) {
        return parseFormula(populatedImList);
    }

    /**
     *
     * @param cnf_inp
     * @return populatedImList containing clauses
     */
    public ImList<Clause> getClauseList(File cnf_inp) {
        EmptyImList<Clause> init = new EmptyImList<Clause>();
        boolean imListNotPopulated = true;

        try (BufferedReader br = new BufferedReader(new FileReader(cnf_inp))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (imListNotPopulated) {
                    if (line.matches("\\d+")) {
                        populatedImList  = init.add(parseClause(line));
                        imListNotPopulated = false;
                    }
                }
                else {
                    populatedImList.add(parseClause(line));
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        return populatedImList;

    }

    /**
     * Parse each line in the CNF file to a clause
     *
     * @param line
     * @return clause
     */
    public Clause parseClause(String line) {
        String[] vars = line.split("\\s+");
        Clause c = new Clause();
        for (String l : vars) {
            if (l.matches("[-]\\d+")) {
                c.add(NegLiteral.make(l.substring(1)));
            }
            else {
                if (l == "0") {
                    return c;
                }
                else {
                    c.add(PosLiteral.make(l));
                }
            }
        }
        return c;
    }

    /**
     * Parse clauses into a single formula
     *
     * @param e
     * @return Formula
     */
    public Formula parseFormula(ImList<Clause> e) {
        Formula f = new Formula();
        for (Clause c : e) {
            f = f.addClause(c);
        }
        return f;
    }
}
