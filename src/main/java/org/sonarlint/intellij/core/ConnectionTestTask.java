/**
 * SonarLint for IntelliJ IDEA
 * Copyright (C) 2015 SonarSource
 * sonarlint@sonarsource.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonarlint.intellij.core;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;
import org.sonarlint.intellij.SonarApplication;
import org.sonarlint.intellij.config.global.SonarQubeServer;
import org.sonarlint.intellij.util.SonarLintUtils;
import org.sonarsource.sonarlint.core.client.api.connected.ConnectedGlobalConfiguration;
import org.sonarsource.sonarlint.core.client.api.connected.ServerConfiguration;
import org.sonarsource.sonarlint.core.client.api.connected.ValidationResult;
import org.sonarsource.sonarlint.core.client.api.connected.WsHelper;
import org.sonarsource.sonarlint.core.container.connected.WsHelperImpl;

public class ConnectionTestTask extends com.intellij.openapi.progress.Task.Modal {
  private static final Logger LOGGER = Logger.getInstance(ConnectionTestTask.class);
  private final SonarQubeServer server;
  private Exception exception;
  private ValidationResult result;

  public ConnectionTestTask(SonarQubeServer server) {
    super(null, "Test connection to SonarQube server", true);
    this.server = server;
  }

  @Override
  public void run(@NotNull ProgressIndicator indicator) {
    indicator.setText("Connecting to " + server.getHostUrl() + "...");
    indicator.setIndeterminate(true);

    try {
      Path tmp = Files.createTempDirectory("sonarlint-test");
      ConnectedGlobalConfiguration globalConfig = ConnectedGlobalConfiguration.builder()
        .setServerId("test")
        .setWorkDir(tmp)
        .build();

      ServerConfiguration serverConfiguration = SonarLintUtils.getServerConfiguration(server);
      WsHelper wsHelper = new WsHelperImpl();
      result = wsHelper.validateConnection(serverConfiguration);
    } catch (Exception e) {
      LOGGER.info("Connection test failed", e);
      exception = e;
    }
  }

  public Exception getException() {
    return exception;
  }

  public ValidationResult result() {
    return result;
  }

}

