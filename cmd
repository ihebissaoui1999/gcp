    1  gcloud container clusters get-credentials devoteamticketcluster --region us-central1 --project strong-bus-460110-p0
    2  kubectl get no
    3  gcloud container clusters get-credentials devoteamticketcluster --region us-central1 --project strong-bus-460110-p0
    4  kubectl get no
    5  ls
    6  kubectl get po
    7  kubectl get ns
    8  kubectl get nodes
    9  kubectl get no
   10  kubectl get svc
   11  docker
   12  alias k=kubectl
   13  k get no
   14  kubectl get po -A
   15  gcloud container clusters create k8s  --num-nodes 2  --cluster-version latest --zone asia-east1-c
   16  kubectl get no
   17  k get no
   18  kubectl get ns
   19  kubectl get po 
   20  kubectl get svc
   21  ls
   22  ls chatintell/
   23  ls devo-front/
   24  ls keycloak-26.1.1/
   25  ls
   26  ls chatintell/
   27  ls devo-front/
   28  ls keycloak-26.1.1/
   29  ls -ltr
   30  cat README-cloudshell.txt 
   31  ls -ltr
   32  docker images
   33  docker images -a
   34  docker images 
   35  docker ps
   36  docker images
   37  docker images -a
   38  docker images
   39  cd chatintell/
   40  ls
   41  ll
   42  vim dockerfile
   43  docker build -h
   44  docker build .
   45  vim dockerfile
   46  docker build .
   47  cat dockerfile 
   48  vim dockerfile
   49  docker build .
   50  vim dockerfile
   51  docker build .
   52  vim dockerfile
   53  docker build .
   54  vim dockerfile
   55  docker build .
   56  vim dockerfile
   57  docker build .
   58  vim dockerfile
   59  docker build .
   60  vim dockerfile
   61  docker build .
   62  vim dockerfile
   63  docker build .
   64  vim dockerfile
   65  docker build .
   66  vim dockerfile
   67  docker build .
   68  vim dockerfile
   69  docker build .
   70  vim dockerfile
   71  docker build .
   72  vim dockerfile
   73  docker build .
   74  vim dockerfile
   75  docker build .
   76  vim dockerfile
   77  docker build .
   78  vim dockerfile
   79  docker build .
   80  vim dockerfile
   81  docker build .
   82  vim dockerfile
   83  docker build .
   84  vim dockerfile
   85  docker build .
   86  vim dockerfile
   87  docker build .
   88  vim dockerfile
   89  docker build .
   90  vim dockerfile
   91  docker build .
   92  vim dockerfile
   93  docker build .
   94  vim dockerfile
   95  docker build .
   96  vim dockerfile
   97  docker build .
   98  vim dockerfile
   99  docker build .
  100  docker image
  101  docker images
  102  docker tag  3a53b191843a frt:v0
  103  docker images
  104  docker tag  3a53b191843a drt/frt:v0
  105  docker images
  106  cd ..
  107  ls
  108  cd devo-front/
  109  vim deockerfile
  110  mv deockerfile dockerfile
  111  vim dockerfile 
  112  vim nginx-custom.conf
  113  vim dockerfile 
  114  vim nginx-custom.conf
  115  vim dockerfile 
  116  cd ../
  117  vim chatintell/dockerfile 
  118  cd chatintell/
  119  docker build . -t frt:v1
  120  docker images
  121  docker rmi frt:v0
  122  docker images
  123  docker rmi drt/frt:v0
  124  docker images
  125  cd ..
  126  cd devo-front/
  127  ls
  128  ll
  129  vim nginx-custom.conf 
  130  docker build -t an:v0 .
  131  vim dockerfile 
  132  docker build -t an:v0 .
  133  vim dockerfile 
  134  docker images
  135  kubectl get no$
  136  kubectl get no
  137  kubectl get ns
  138  kubectl create ns front
  139  kubectl create ns backend
  140  docker images
  141  vim pod-front.yaml
  142  kubectl apply -f pod-front.yaml 
  143  vim pod-front.yaml
  144  kubectl apply -f pod-front.yaml 
  145  kubectl get po -n front
  146  kubectl descrieb po front -n front
  147  kubectl describe po front -n front
  148  vim pod-front.yaml
  149  kubectl apply -f pod-front.yaml 
  150  kubectl describe po front -n front
  151  kubectl get po -n front
  152  cp pod-front.yaml pod-back.yaml
  153  vim pod-back.yaml 
  154  kubectl get ns
  155  kubectl apply -f  pod-back.yaml 
  156  kubectl get po -n backend
  157  kubectl get po -n backend -w
  158  kubectl get po -n backend
  159  kubectl describe  po back -n backend
  160  kubectl logs  back -n backend
  161  cp pod-back.yaml pod-mysql.yaml
  162  kubectl get po -n backend -owide
  163  kubectl get po -n front -owide
  164  vim pod-mysql.yaml 
  165  kubectl apply -f  pod-mysql.yaml 
  166  kubectl get po -n backend -owide
  167  kubectl get po -n backend -owide -w
  168  kubectl get po -n backend -owide
  169  kubectl get po -n backend 
  170  kubectl describe po mysql -n backend 
  171  kubectl logs  mysql -n backend 
  172  vim pod-mysql.yaml 
  173  kubectl apply -f  pod-mysql.yaml 
  174  vim pod-mysql.yaml 
  175  kubectl apply -f  pod-mysql.yaml 
  176  kubectl delete  -f  pod-mysql.yaml 
  177  kubectl apply -f  pod-mysql.yaml 
  178  kubectl get po -n backend 
  179  kubectl logs  mysql -n backend 
  180  vim pod-mysql.yaml 
  181  kubectl logs  mysql -n backend 
  182  kubectl delete  -f  pod-mysql.yaml 
  183  kubectl apply -f  pod-mysql.yaml 
  184  vim pod-mysql.yaml 
  185  kubectl apply -f  pod-mysql.yaml 
  186  kubectl delete  -f  pod-mysql.yaml 
  187  vim pod-mysql.yaml 
  188  kubectl apply -f  pod-mysql.yaml 
  189  vim pod-mysql.yaml 
  190  kubectl apply -f  pod-mysql.yaml 
  191  vim pod-mysql.yaml 
  192  kubectl apply -f  pod-mysql.yaml 
  193  kubectl logs  mysql -n backend 
  194  kubectl get po -n backend 
  195  kubectl delete pod back -n backend 
  196  kubectl get po -n backend 
  197  kubectl apply -f  pod-back.yaml 
  198  kubectl get po -n backend 
  199  kubectl get po -n backend  -w
  200  kubectl logs back -n backend 
  201  kubectl logs back -n backend -f
  202  kubectl get po -n backend  
  203  kubectl get svc -n backend  
  204  kubectl exec -it mysql -n backend   /bin/bash
  205  kubectl exec -it mysql -n backend --  /bin/bash
  206  vim pod-back.yaml 
  207  vim pod-mysql.yaml 
  208  vim pod-back.yaml 
  209  kubectl apply -f  pod-back.yaml 
  210  kubectl get po -n backend  
  211  kubectl delete  -f  pod-back.yaml 
  212  kubectl get po -n backend  
  213  kubectl apply -f  pod-back.yaml 
  214  kubectl get po -n backend  
  215  kubectl get po -n backend  -w
  216  kubectl get po -n backend  
  217  #kubectl delete po mysql -n backend  
  218  kubectl get po -n backend  -owide
  219  kubectl delete po mysql -n backend  
  220  kubectl apply -f  pod-mysql.yaml 
  221  kubectl get po -n backend  -owide
  222  kubectl delete po back -n backend  
  223  vim pod-back.yaml 
  224  kubectl apply -f  pod-back.yaml
  225  kubectl get po -n backend  -w
  226  kubectl logs back -n backend -f
  227  vim svc-back.yaml
  228  vim deploy-back.yaml 
  229  vim svc-back.yaml
  230  vim deploy-back.yaml 
  231  vim svc-back.yaml
  232  vim deploy-back.yaml 
  233  vim svc-mysql.yaml
  234  vim deploy-mysql.yaml
  235  vim pod-mysql.yaml 
  236  vim deploy-mysql.yaml
  237  vim deploy-back.yaml 
  238  vim deploy-mysql.yaml
  239  vim svc-back.yaml 
  240  vim svc-mysql.yaml 
  241  ll
  242  mkdir deploy 
  243  mv svc-back.yaml deploy-back.yaml deploy
  244  mkdir deploymysql
  245  mv svc-mysql.yaml deploy-mysql.yaml deploymysql/
  246  ll
  247  mv deploy deployback
  248  ll
  249  kubectl delete  -f  pod-back.yaml 
  250  kubectl delete  -f  pod-mysql.yaml.yaml 
  251  kubectl delete  -f  pod-mysql.yaml 
  252  kubectl get po -n backend 
  253  kubectl apply -f deploymysql/deploy-mysql.yaml 
  254  kubectl apply -f deploymysql/svc-mysql.yaml 
  255  kubectl get po -n backend 
  256  kubectl get svc -n backend 
  257  kubectl get ep  -n backend 
  258  kubectl get po -n backend -owide
  259* 
  260  kubectl get po -n backend -owide
  261  vim deploymysql/deploy-mysql.yaml 
  262  vim deployback/deploy-back.yaml 
  263  kubectl apply -f  deployback/deploy-back.yaml 
  264  kubectl apply -f  deployback/svc-back.yaml 
  265  kubectl get po -n backend -owide
  266  kubectl get po -n backend -w
  267  watch kubectl get po -n backend 
  268  kubectl logs backend-deployment-f598d9c8d-fksgz -n backend 
  269  kubectl get po -n backend -w
  270  kubectl get po -n backend 
  271  vim deploymysql/deploy-mysql.yaml 
  272  kubectl diff -f  deploymysql/deploy-mysql.yaml 
  273  vim deploymysql/deploy-mysql.yaml 
  274  kubectl apply -f  deploymysql/deploy-mysql.yaml 
  275  kubectl get po -n backend 
  276  kubectl delete deploy mysql -n backend 
  277  kubectl get po -n backend 
  278  kubectl delete backend-deployment-f598d9c8d-fksgz -n backend 
  279  kubectl delete po backend-deployment-f598d9c8d-fksgz -n backend 
  280  kubectl get po -n backend 
  281  watch kubectl get po -n backend 
  282  kubectl delete deploy mysqldb -n backend 
  283  kubectl get po -n backend 
  284  kubectl delete po backend-deployment-f598d9c8d-rg2lq  -n backend 
  285  kubectl get po -n backend 
  286  kubectl get po -n backend -w
  287  vim deploymysql/deploy-mysql.yaml 
  288  #kubectl apply -f  deploymysql/deploy-mysql.yaml 
  289  vim deploymysql/deploy-mysql.yaml 
  290  kubectl apply -f  deploymysql/deploy-mysql.yaml 
  291  kubectl get po -n backend -w
  292  vim deploymysql/deploy-mysql.yaml 
  293  kubectl get po -n backend -w
  294  kubectl get po -n backend
  295  kubectl logs backend-deployment-f598d9c8d-4h6kx -n backend
  296  kubectl get po -n backend
  297  kubectl get deploy  -n backend
  298  kubectl delete po  backend-deployment-f598d9c8d-4h6kx -n backend
  299  kubectl get po -n backend
  300  kubectl logs backend-deployment-f598d9c8d-thvrz -n backend
  301  kubectl get po -n backend
  302  vim deploymysql/deploy-mysql.yaml 
  303  kubectl apply -f  deploymysql/deploy-mysql.yaml 
  304  kubectl get po -n backend
  305  kubectl rollout restart backend-deployment -n backend
  306  kubectl get po -n backend
  307  k logs backend-deployment-f598d9c8d-thvrz -n backend 
  308  kubectl  logs backend-deployment-f598d9c8d-thvrz -n backend 
  309  vim deploymysql/deploy-mysql.yaml 
  310  kubectl -f  deploymysql/deploy-mysql.yaml 
  311  kubectl apply  -f  deploymysql/deploy-mysql.yaml 
  312  kubectl rollout restart backend-deployment -n backend
  313  kubectl get po -n backend
  314  kubectl delete deploy musql -n bakcend 
  315  kubectl delete deploy musql -n backend 
  316  kubectl get po -n backend
  317  kubectl delete deploy mysql -n backend 
  318  kubectl get po -n backend
  319  kubectl rollout restart backend-deployment -n backend
  320  kubectl get po -n backend
  321  k delete po backend-deployment-f598d9c8d-thvrz -n backend 
  322  kubeclt  delete po backend-deployment-f598d9c8d-thvrz -n backend 
  323  kubectl delete po backend-deployment-f598d9c8d-thvrz -n backend 
  324  k delete po backend-deployment-f598d9c8d-thvrz -n backend 
  325  kubectl get po -n backend
  326* 
  327  kubectl  logs backend-deployment-f598d9c8d-84njd -n backend 
  328  k logs backend-deployment-f598d9c8d-84njd -n backend 
  329  kubectl get po -n backend
  330  vim deploymysql/deploy-mysql.yaml 
  331  kubectl apply -f  deploymysql/deploy-mysql.yaml 
  332  vim deploymysql/deploy-mysql.yaml 
  333  vim deploymysql/svc-mysql.yaml 
  334  kubectl apply -f  deploymysql/svc-mysql.yaml 
  335  kubectl get svc  -n backend
  336  kubectl delete  svc  mysql -n backend
  337  kubectl get svc  -n backend
  338  kubectl get po -n backend
  339  kubectl  delete po backend-deployment-f598d9c8d-84njd -n backend 
  340  kubectl get po -n backend
  341  watch kubectl get po -n backend
  342  kubectl logs backend-deployment-f598d9c8d-rv6t5 -n backend 
  343  history 
  344  history > cmd
