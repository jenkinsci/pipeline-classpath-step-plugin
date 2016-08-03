package cprice404.plugins.workflow.steps;

import static org.junit.Assert.*;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;

import org.jenkinsci.plugins.workflow.actions.LogAction;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.graph.FlowGraphWalker;
import org.jenkinsci.plugins.workflow.graph.FlowNode;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

public class AddToClasspathStepTest {

    @ClassRule public static BuildWatcher buildWatcher = new BuildWatcher();
    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Test
    public void smokes() throws Exception {
        WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "p");
        File resourcesDir = new File("./src/test/resources/cprice404/plugins/workflow/steps/add_to_classpath_step_test");
        p.setDefinition(new CpsFlowDefinition("stage 'hello there'\n" +
                "addToClasspath '" + resourcesDir.getAbsolutePath() + "/src'\n" +
                "echo 'classpath updated'\n" +
                "node {\n" +
                "  foo = load '" + resourcesDir.getAbsolutePath() + "/scripts/foo.groovy'\n" +
                "}\n" +
                "echo foo",
                true));
        WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));
        List<LogAction> logActions = new ArrayList<LogAction>();
        for (FlowNode n : new FlowGraphWalker(b.getExecution())) {
            LogAction la = n.getAction(LogAction.class);
            if (la != null) {
                logActions.add(la);
            }
        }
        StringWriter w = new StringWriter();
        logActions.get(0).getLogText().writeLogTo(0, w);
        System.out.println("LOG MESSAGE:" + w.toString().trim());
        assertEquals("Hello From Test Class!", w.toString().trim());
    }

}
