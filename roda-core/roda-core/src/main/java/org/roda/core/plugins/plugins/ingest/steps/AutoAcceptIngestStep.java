package org.roda.core.plugins.plugins.ingest.steps;

import java.util.Map;

import org.roda.core.RodaCoreFactory;
import org.roda.core.data.common.RodaConstants;
import org.roda.core.data.exceptions.AuthorizationDeniedException;
import org.roda.core.data.exceptions.GenericException;
import org.roda.core.data.exceptions.JobException;
import org.roda.core.data.exceptions.NotFoundException;
import org.roda.core.data.exceptions.RequestNotValidException;
import org.roda.core.data.v2.jobs.Job;
import org.roda.core.plugins.plugins.PluginHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoAcceptIngestStep extends IngestStep {
  private static final Logger LOGGER = LoggerFactory.getLogger(AutoAcceptIngestStep.class);

  public AutoAcceptIngestStep(String pluginName, String parameterName, boolean usesCorePlugin, boolean mandatory,
    boolean needsAips, boolean removesAIPs) {
    super(pluginName, parameterName, usesCorePlugin, mandatory, needsAips, removesAIPs);
  }

  public AutoAcceptIngestStep(String pluginName, String parameterName, boolean usesCorePlugin, boolean mandatory,
    boolean needsAips, boolean removesAIPs, Map<String, String> parameters) {
    super(pluginName, parameterName, usesCorePlugin, mandatory, needsAips, removesAIPs, parameters);
  }

  @Override
  public void execute(IngestExecutePack pack) throws JobException {
    if (PluginHelper.verifyIfStepShouldBePerformed(pack.getIngestPlugin(), pack.getPluginParameter(),
      !this.usesCorePlugin() ? this.getPluginName() : null)) {
      IngestStepsUtils.executePlugin(pack, this);
      PluginHelper.updateJobInformationAsync(pack.getIngestPlugin(),
        pack.getJobPluginInfo().incrementStepsCompletedByOne());

      if (RodaCoreFactory.getRodaConfiguration()
        .getBoolean(RodaConstants.CORE_TRANSFERRED_RESOURCES_INGEST_MOVE_WHEN_AUTOACCEPT, false)) {
        PluginHelper.moveSIPs(pack.getIngestPlugin(), pack.getModel(), pack.getIndex(), pack.getResources(),
          pack.getJobPluginInfo());
      }
    } else {
      try {
        Job cachedJob = PluginHelper.getJob(pack.getIngestPlugin(), pack.getModel());
        IngestStepsUtils.updateAIPsToBeAppraised(pack, cachedJob);
      } catch (NotFoundException | GenericException | RequestNotValidException | AuthorizationDeniedException e) {
        LOGGER.error("Error updating AIPs to be appraised", e);
      }
    }
  }
}
