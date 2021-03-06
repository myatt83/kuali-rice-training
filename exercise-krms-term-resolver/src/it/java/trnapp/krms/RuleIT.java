package trnapp.krms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kuali.rice.krms.api.KrmsApiServiceLocator;
import org.kuali.rice.krms.api.engine.Engine;
import org.kuali.rice.krms.api.engine.EngineResults;
import org.kuali.rice.krms.api.engine.ExecutionFlag;
import org.kuali.rice.krms.api.engine.ExecutionOptions;
import org.kuali.rice.krms.api.engine.Facts;
import org.kuali.rice.krms.api.engine.ResultEvent;
import org.kuali.rice.krms.api.engine.SelectionCriteria;

import trnapp.BaseITCase;

public class RuleIT extends BaseITCase {

	private static final String NAMESPACE_CODE_SELECTOR = "namespaceCode";
	private static final String NAME_SELECTOR = "name";
	
	// Term Names
	
	private static final String AFFILIATION = "Affiliation";
	private static final String IN_GOOD_STANDING = "In Good Standing";
	private static final String LAST_WEEK_OF_SEMESTER = "Last Week of Semester";
	private static final String LOAN_COMPLIES_WITH_LICENSE_TERMS = "Loan Complies with License Terms";
	private static final String NUMBER_OF_INSTANCES_ON_LOAN = "Number of Instances on Loan";
	
	/**
	 * Tests a patron who is not in good standing.
	 */
	@Test
	public void testPatronInBadStanding() {		
		
		SelectionCriteria criteria =
				SelectionCriteria.createCriteria(null, selectOleContext(), selectPolicyCirculationAgenda());
		
		Facts.Builder factBuilder = Facts.Builder.create();
		
		factBuilder.addFact(IN_GOOD_STANDING, "false");
		factBuilder.addFact(LOAN_COMPLIES_WITH_LICENSE_TERMS, "true");
		factBuilder.addFact(AFFILIATION, "undergrad");
		factBuilder.addFact(NUMBER_OF_INSTANCES_ON_LOAN, 10);
		factBuilder.addFact(LAST_WEEK_OF_SEMESTER, "false");
		
		ExecutionOptions options = new ExecutionOptions();
		options.setFlag(ExecutionFlag.LOG_EXECUTION, true);
		
		EngineResults results = getEngine().execute(criteria, factBuilder.build(), options);
		
		assertFalse("Results list should not be empty.", results.getAllResults().isEmpty());
		
		// only the first rule should have actually been executed because of how the agenda is constructed
		List<ResultEvent> rulesEvaluated = results.getResultsOfType(ResultEvent.RULE_EVALUATED);
		assertEquals(1, rulesEvaluated.size());
		ResultEvent ruleEvaluated = rulesEvaluated.get(0);
		assertFalse("rule should have evaluated to false", ruleEvaluated.getResult());
		
	}

	/**
	 * Tests a patron who is not in good standing.
	 */
	@Test
	public void testLoanDoesNotComplyWithLicenseTerms() {		
		
		SelectionCriteria criteria =
				SelectionCriteria.createCriteria(null, selectOleContext(), selectPolicyCirculationAgenda());
		
		Facts.Builder factBuilder = Facts.Builder.create();
		
		factBuilder.addFact(IN_GOOD_STANDING, "true");
		factBuilder.addFact(LOAN_COMPLIES_WITH_LICENSE_TERMS, "false");
		factBuilder.addFact(AFFILIATION, "undergrad");
		factBuilder.addFact(NUMBER_OF_INSTANCES_ON_LOAN, 10);
		factBuilder.addFact(LAST_WEEK_OF_SEMESTER, "false");
		
		ExecutionOptions options = new ExecutionOptions();
		options.setFlag(ExecutionFlag.LOG_EXECUTION, true);
		
		EngineResults results = getEngine().execute(criteria, factBuilder.build(), options);
		
		assertFalse("Results list should not be empty.", results.getAllResults().isEmpty());
		
		// only the first rule should have actually been executed because of how the agenda is constructed
		List<ResultEvent> rulesEvaluated = results.getResultsOfType(ResultEvent.RULE_EVALUATED);
		assertEquals(1, rulesEvaluated.size());
		ResultEvent ruleEvaluated = rulesEvaluated.get(0);
		assertFalse("rule should have evaluated to false", ruleEvaluated.getResult());
		
	}

	/**
	 * Tests a situation where an undergrad has too many instances on loan.
	 */
	@Test
	public void testUndergradHasTooManyInstancesOnLoan() {		
		
		SelectionCriteria criteria =
				SelectionCriteria.createCriteria(null, selectOleContext(), selectPolicyCirculationAgenda());
		
		Facts.Builder factBuilder = Facts.Builder.create();
		
		factBuilder.addFact(IN_GOOD_STANDING, "true");
		factBuilder.addFact(LOAN_COMPLIES_WITH_LICENSE_TERMS, "true");
		factBuilder.addFact(AFFILIATION, "undergrad");
		factBuilder.addFact(NUMBER_OF_INSTANCES_ON_LOAN, 100);
		factBuilder.addFact(LAST_WEEK_OF_SEMESTER, "false");
		
		ExecutionOptions options = new ExecutionOptions();
		options.setFlag(ExecutionFlag.LOG_EXECUTION, true);
		
		EngineResults results = getEngine().execute(criteria, factBuilder.build(), options);
		
		assertFalse("Results list should not be empty.", results.getAllResults().isEmpty());
		
		// two rules should be evaluated, the top level rule and the instances on loan rule
		List<ResultEvent> rulesEvaluated = results.getResultsOfType(ResultEvent.RULE_EVALUATED);
		assertEquals(2, rulesEvaluated.size());
		ResultEvent ruleEvaluated = rulesEvaluated.get(0);
		assertTrue("rule should have evaluated to true", ruleEvaluated.getResult());
		ruleEvaluated = rulesEvaluated.get(1);
		assertFalse("rule should have evaluated to false", ruleEvaluated.getResult());
		
	}
	
	/**
	 * This test should test a situation where an undergrad is attempting to loan and it is the last week of the semester.
	 * Please set up the facts such that it ensures that all rules in the agenda are executed (so when you do the comparison
	 * of the number of rules that have been evaluated, it should come out to 6!).
	 * 
	 * Be sure to check each individual rule result event (as mentioned before, there should be 6 of them) to assert whether
	 * or not it evaluated to true or false.  Note that the result events should come through in the order the rules are
	 * evaluated in the agenda.
	 */
	@Test
	public void testUndergradDuringLastWeekOfSemester() {		
		
		SelectionCriteria criteria =
				SelectionCriteria.createCriteria(null, selectOleContext(), selectPolicyCirculationAgenda());
		
		Facts.Builder factBuilder = Facts.Builder.create();
		
		factBuilder.addFact(IN_GOOD_STANDING, "true");
		factBuilder.addFact(LOAN_COMPLIES_WITH_LICENSE_TERMS, "true");
		factBuilder.addFact(AFFILIATION, "undergrad");
		factBuilder.addFact(NUMBER_OF_INSTANCES_ON_LOAN, 10);
		factBuilder.addFact(LAST_WEEK_OF_SEMESTER, "true");
		
		ExecutionOptions options = new ExecutionOptions();
		options.setFlag(ExecutionFlag.LOG_EXECUTION, true);
		
		EngineResults results = getEngine().execute(criteria, factBuilder.build(), options);
		
		assertFalse("Results list should not be empty.", results.getAllResults().isEmpty());
		
		// two rules should be evaluated, the top level rule and the instances on loan rule
		List<ResultEvent> rulesEvaluated = results.getResultsOfType(ResultEvent.RULE_EVALUATED);
		assertEquals(6, rulesEvaluated.size());
		ResultEvent ruleEvaluated = rulesEvaluated.get(0);
		assertTrue("rule should have evaluated to true", ruleEvaluated.getResult());
		ruleEvaluated = rulesEvaluated.get(1);
		assertTrue("rule should have evaluated to true", ruleEvaluated.getResult());
		ruleEvaluated = rulesEvaluated.get(2);
		assertFalse("rule should have evaluated to false", ruleEvaluated.getResult());
		ruleEvaluated = rulesEvaluated.get(3);
		assertTrue("rule should have evaluated to true", ruleEvaluated.getResult());
		ruleEvaluated = rulesEvaluated.get(4);
		assertTrue("rule should have evaluated to true", ruleEvaluated.getResult());
		ruleEvaluated = rulesEvaluated.get(5);
		assertFalse("rule should have evaluated to false", ruleEvaluated.getResult());
		
		// now verify that canCirculate and loanPeriod are set properly
		assertEquals(true, results.getAttribute("canCirculate"));
		assertEquals(30, results.getAttribute("loanPeriod"));
		
	}
	
	@Test
	public void testLoanToResident() {
		SelectionCriteria criteria =
				SelectionCriteria.createCriteria(null, selectOleContext(), selectPolicyCirculationAgenda());
		
		Facts.Builder factBuilder = Facts.Builder.create();
		
		factBuilder.addFact(IN_GOOD_STANDING, "true");
		factBuilder.addFact(LOAN_COMPLIES_WITH_LICENSE_TERMS, "true");
		factBuilder.addFact(AFFILIATION, "resident");
		factBuilder.addFact(NUMBER_OF_INSTANCES_ON_LOAN, 5);
		factBuilder.addFact(LAST_WEEK_OF_SEMESTER, "false");
		
		ExecutionOptions options = new ExecutionOptions();
		options.setFlag(ExecutionFlag.LOG_EXECUTION, true);
		
		EngineResults results = getEngine().execute(criteria, factBuilder.build(), options);
		assertEquals(true, results.getAttribute("canCirculate"));
		assertEquals(30, results.getAttribute("loanPeriod"));
	}
	
	@Test
	public void testLoanToUndergradNotEndOfSemester() {
		SelectionCriteria criteria =
				SelectionCriteria.createCriteria(null, selectOleContext(), selectPolicyCirculationAgenda());
		
		Facts.Builder factBuilder = Facts.Builder.create();
		
		factBuilder.addFact(IN_GOOD_STANDING, "true");
		factBuilder.addFact(LOAN_COMPLIES_WITH_LICENSE_TERMS, "true");
		factBuilder.addFact(AFFILIATION, "undergrad");
		factBuilder.addFact(NUMBER_OF_INSTANCES_ON_LOAN, 5);
		factBuilder.addFact(LAST_WEEK_OF_SEMESTER, "false");
		
		ExecutionOptions options = new ExecutionOptions();
		options.setFlag(ExecutionFlag.LOG_EXECUTION, true);
		
		EngineResults results = getEngine().execute(criteria, factBuilder.build(), options);
		assertEquals(true, results.getAttribute("canCirculate"));
		assertEquals(45, results.getAttribute("loanPeriod"));
	}

	@Test
	public void testLoanToFaculty() {
		SelectionCriteria criteria =
				SelectionCriteria.createCriteria(null, selectOleContext(), selectPolicyCirculationAgenda());
		
		Facts.Builder factBuilder = Facts.Builder.create();
		
		factBuilder.addFact(IN_GOOD_STANDING, "true");
		factBuilder.addFact(LOAN_COMPLIES_WITH_LICENSE_TERMS, "true");
		factBuilder.addFact(AFFILIATION, "faculty");
		factBuilder.addFact(NUMBER_OF_INSTANCES_ON_LOAN, 5);
		factBuilder.addFact(LAST_WEEK_OF_SEMESTER, "false");
		
		ExecutionOptions options = new ExecutionOptions();
		options.setFlag(ExecutionFlag.LOG_EXECUTION, true);
		
		EngineResults results = getEngine().execute(criteria, factBuilder.build(), options);
		assertEquals(true, results.getAttribute("canCirculate"));
		assertEquals(120, results.getAttribute("loanPeriod"));
	}
	
	protected Engine getEngine() {
		return KrmsApiServiceLocator.getEngine();
	}
	
	protected Map<String, String> selectOleContext() {
		Map<String, String> selector = new HashMap<String, String>();
		selector.put(NAMESPACE_CODE_SELECTOR, "OLE");
		selector.put(NAME_SELECTOR, "Kuali OLE");
		return selector;
	}
	
	protected Map<String, String> selectPolicyCirculationAgenda() {
	    Map<String, String> selector = new HashMap<String, String>();
	    selector.put(NAME_SELECTOR, "Circulation Policy");
	    return selector;
	}	
	
	
}
