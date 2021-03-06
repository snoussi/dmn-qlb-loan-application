package com.redhat.btison.dm7.dmn.qlb;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;

public class ApplicantInitialisationTest {

    @Test
    public void testApplicationInitialisation() {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.newKieClasspathContainer();
        KieSession kieSession = kieContainer.newKieSession();

        DMNRuntime dmnRuntime = kieSession.getKieRuntime(DMNRuntime.class);

        String dmnModelNamespace = "http://www.trisotech.com/dmn/definitions/_c55e5995-0cc9-40b8-b783-88468c69ebca";
        String dmnModelName = "qlb-loan-application";

        DMNModel dmnModel = dmnRuntime.getModel(dmnModelNamespace, dmnModelName);

        assertThat(dmnModel, notNullValue());
        assertThat( dmnModel.hasErrors(), is(false) );

        DMNContext dmnContext = dmnRuntime.newContext();
        dmnContext.set("Applicant", loadApplicant());

        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, dmnContext);
        DMNDecisionResult decisionResult = dmnResult.getDecisionResultByName("Applicant Initialisation");
        assertThat(decisionResult.getEvaluationStatus(), equalTo(SUCCEEDED));
        assertThat(decisionResult.getResult(), instanceOf(Map.class));
        assertThat(decisionResult.getResult(), equalTo(loadExpectedResult()));
    }

    private Map<String, Object> loadApplicant() {
        Map<String, Object> applicant = new HashMap<>(  );
        applicant.put("name", "John Doe");
        applicant.put("age", number(25));
        applicant.put("credit score", number(100));
        applicant.put("yearly income", number(120000));
        return applicant;
    }

    private Map<String, Object> loadExpectedResult() {
        Map<String, Object> applicant = new HashMap<>(  );
        applicant.put("name", "John Doe" );
        applicant.put("age", number(25));
        applicant.put("credit score", number(100));
        applicant.put("yearly income", number(120000));
        applicant.put("eligible", true);
        applicant.put("monthly income", number(10000));
        return applicant;
    }

    private BigDecimal number(Number n ) {
        return BigDecimal.valueOf( n.longValue() );
    }

}
