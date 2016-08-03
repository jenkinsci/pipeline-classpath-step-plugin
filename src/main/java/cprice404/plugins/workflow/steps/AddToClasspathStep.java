package cprice404.plugins.workflow.steps;

import groovy.lang.GroovyShell;
import hudson.Extension;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.cps.CpsFlowExecution;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.inject.Inject;
import java.io.File;

public class AddToClasspathStep extends AbstractStepImpl {

    private final String path;

    @DataBoundConstructor
    public AddToClasspathStep(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(Execution.class);
        }

        @Override
        public String getFunctionName() {
            return "addToClasspath";
        }

        @Override
        public String getDisplayName() {
            return "Add file path to Groovy classPath";
        }
    }

    public static class Execution extends AbstractSynchronousStepExecution<Void> {

        @Inject private transient AddToClasspathStep step;
        @StepContextParameter private transient TaskListener listener;
        @StepContextParameter private transient CpsFlowExecution cpsFlowExecution;

        @Override protected Void run() throws Exception {
            GroovyShell shell = cpsFlowExecution.getShell();
            shell.getClassLoader().addURL(
                    new File(step.getPath()).toURI().toURL());

            listener.getLogger().println(step.getPath());
            return null;
        }

        private static final long serialVersionUID = 1L;

    }

}
