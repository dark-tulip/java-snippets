package com.epam;


@Singleton
//@Deprecated // AnnouncerImpl -- (recommend()) ->
            // Proxy -- (invoke()) ->
            // InvocationHandler -- (вся магия тута recommend()) -> RecommendatorImpl
public class RecommendatorImpl implements Recommendator {
  @InjectProperty("wisky")
  private String alcohol;

  public RecommendatorImpl() {
    System.out.println("Recommendator was created");
  }
  @Override
  public void recommend() {
    System.out.println("To save from corona drink, " + alcohol);
  }
}
