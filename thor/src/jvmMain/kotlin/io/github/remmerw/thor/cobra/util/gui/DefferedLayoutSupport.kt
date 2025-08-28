package io.github.remmerw.thor.cobra.util.gui;

import java.util.concurrent.Future;

public interface DefferedLayoutSupport {
    Future<Boolean> layoutCompletion();
}
