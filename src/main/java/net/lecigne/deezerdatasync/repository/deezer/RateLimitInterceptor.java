package net.lecigne.deezerdatasync.repository.deezer;

import com.google.common.util.concurrent.RateLimiter;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
class RateLimitInterceptor implements Interceptor {

  private final RateLimiter limiter;

  public RateLimitInterceptor(double permitsPerSecond) {
    this.limiter = RateLimiter.create(permitsPerSecond);
  }

  @NotNull
  @Override
  public Response intercept(Chain chain) throws IOException {
    limiter.acquire();
    return chain.proceed(chain.request());
  }

}
