package org.roda.core.plugins.plugins.ingest.steps;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.roda.core.data.exceptions.JobException;
import org.roda.core.plugins.plugins.PluginHelper;

public class IngestStep implements IngestCommand {

  private String pluginName;
  private String parameterName;
  private boolean usesCorePlugin;
  private boolean mandatory;
  private boolean needsAips;
  private boolean removesAips;
  private Map<String, String> parameters;
  private Function<IngestExecutePack, Void> genericFunction = null;

  public IngestStep(Function<IngestExecutePack, Void> function) {
    this.genericFunction = function;
  }

  public IngestStep(String pluginName, String parameterName, boolean usesCorePlugin, boolean mandatory,
    boolean needsAips, boolean removesAIPs) {
    this(pluginName, parameterName, usesCorePlugin, mandatory, needsAips, removesAIPs, new HashMap<>());
  }

  public IngestStep(String pluginName, String parameterName, boolean usesCorePlugin, boolean mandatory,
    boolean needsAips, boolean removesAIPs, Map<String, String> parameters) {
    this.pluginName = pluginName;
    this.parameterName = parameterName;
    this.usesCorePlugin = usesCorePlugin;
    this.mandatory = mandatory;
    this.needsAips = needsAips;
    this.removesAips = removesAIPs;
    this.parameters = parameters;
  }

  public String getPluginName() {
    return pluginName;
  }

  public void setPluginName(String pluginName) {
    this.pluginName = pluginName;
  }

  public String getParameterName() {
    return parameterName;
  }

  public void setParameterName(String parameterName) {
    this.parameterName = parameterName;
  }

  public boolean usesCorePlugin() {
    return usesCorePlugin;
  }

  public void setUsesCorePlugin(boolean usesCorePlugin) {
    this.usesCorePlugin = usesCorePlugin;
  }

  public boolean isMandatory() {
    return mandatory;
  }

  public void setMandatory(boolean mandatory) {
    this.mandatory = mandatory;
  }

  public boolean needsAips() {
    return needsAips;
  }

  public void setNeedsAips(boolean needsAips) {
    this.needsAips = needsAips;
  }

  public boolean removesAips() {
    return removesAips;
  }

  public void setRemovesAips(boolean removesAips) {
    this.removesAips = removesAips;
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, String> parameters) {
    this.parameters = parameters;
  }

  public Function<IngestExecutePack, Void> getGenericFunction() {
    return genericFunction;
  }

  public void setGenericFunction(Function<IngestExecutePack, Void> genericFunction) {
    this.genericFunction = genericFunction;
  }

  @Override
  public void execute(IngestExecutePack pack) throws JobException {
    if (this.genericFunction != null) {
      genericFunction.apply(pack);
    } else {
      if (PluginHelper.verifyIfStepShouldBePerformed(pack.getIngestPlugin(), pack.getPluginParameter(),
        !this.usesCorePlugin() ? this.getPluginName() : null)) {
        IngestStepsUtils.executePlugin(pack, this);
        PluginHelper.updateJobInformationAsync(pack.getIngestPlugin(),
          pack.getJobPluginInfo().incrementStepsCompletedByOne());
      }
    }
  }
}
