package org.mohans.dev;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderErrors;
import org.drools.rule.*;
import org.drools.rule.Package;
import org.junit.Test;

import org.mohans.dev.message.Message;
import org.mohans.dev.customer.Customer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

//http://www.kijanowski.eu/index.php?site=articles&article=introduction&lang=en
// http://www.youtube.com/watch?v=4rYUYAYeQ1U

public class HelloWorldRuleTest {
    private  String[] RULEFILES = {   "/org/mohans/dev/hello/helloWorld.drl"
	                             , "/org/mohans/dev/hello/actionRule.drl"
				     , "/org/mohans/dev/hello/ageFinderRule.drl"
								 };
    @Test
    public void shouldFireHelloWorld() throws IOException, DroolsParserException {
        RuleBase ruleBase = initialiseDrools();
        WorkingMemory workingMemory = initializeMessageObjects(ruleBase);
        int expectedNumberOfRulesFired = RULEFILES.length;

        int actualNumberOfRulesFired = workingMemory.fireAllRules();

        assertThat(actualNumberOfRulesFired, is(expectedNumberOfRulesFired));
    }

    private RuleBase initialiseDrools() throws IOException, DroolsParserException {
        PackageBuilder packageBuilder = readRuleFiles();
        return addRulesToWorkingMemory(packageBuilder);
    }

    private PackageBuilder readRuleFiles() throws DroolsParserException, IOException {
        PackageBuilder packageBuilder = new PackageBuilder();   

        for (String ruleFile : RULEFILES) {
            Reader reader = getRuleFileAsReader(ruleFile);
            packageBuilder.addPackageFromDrl(reader);
        }		        
        assertNoRuleErrors(packageBuilder);
        return packageBuilder;
    }

    private Reader getRuleFileAsReader(String ruleFile) {
        InputStream resourceAsStream = getClass().getResourceAsStream(ruleFile);

        return new InputStreamReader(resourceAsStream);
    }

    private RuleBase addRulesToWorkingMemory(PackageBuilder packageBuilder) {
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        Package rulesPackage = packageBuilder.getPackage();
        ruleBase.addPackage(rulesPackage);

        return ruleBase;
    }

    private void assertNoRuleErrors(PackageBuilder packageBuilder) {
        PackageBuilderErrors errors = packageBuilder.getErrors();

        if (errors.getErrors().length > 0) {
            StringBuilder errorMessages = new StringBuilder();
            errorMessages.append("Found errors in package builder\n");
            for (int i = 0; i < errors.getErrors().length; i++) {
                DroolsError errorMessage = errors.getErrors()[i];
                errorMessages.append(errorMessage);
                errorMessages.append("\n");
            }
            errorMessages.append("Could not parse knowledge");
            throw new IllegalArgumentException(errorMessages.toString());
        }
    }

    private WorkingMemory initializeMessageObjects(RuleBase ruleBase) {
        WorkingMemory workingMemory = ruleBase.newStatefulSession();

        createHelloWorld(workingMemory);
        createHighValue(workingMemory);
	createCustomer(workingMemory);

        return workingMemory;
    }

    private void createHelloWorld(WorkingMemory workingMemory) {
        Message helloMessage = new Message();
	String msg = "Hello";
        helloMessage.setType(msg);
	helloMessage.setMessageValue(msg.length());
        workingMemory.insert(helloMessage);
    }
	
	
	private void createHighValue(WorkingMemory workingMemory) {
        Message highValue = new Message();
        highValue.setType("High value");
        highValue.setMessageValue(42);
        workingMemory.insert(highValue);
    }
	
	
    private void createCustomer(WorkingMemory workingMemory) {
        Customer customer = new Customer();		
        customer.setAge(new Integer(45) );		
        workingMemory.insert(customer);
    }
	
	
	
}
