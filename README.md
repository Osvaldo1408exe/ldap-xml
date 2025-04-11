# ldap-xml
API desenvolvida em Java para gerenciamento de  usuários e grupos em formato .xml para servidor LDAP 

### Funcionalidades:

- (**Post**)  Inserção de usuários com tratamento de dados 
- (**Get**) Recuperação de todos os usuários e grupos
- (**Delete**) Apagar usuários do servidor
- (**Put**) Modificar usuários e seus grupos vinculados

### Instalação e Configuração
#### Antes de iniciar o projeto certifique que a estrutura padrão do servidor tenha "ou=groups" e o "ou=users"

Após clonar o projeto, configure a conexão no diretório:
````
config\LdapConnection
````
Após iniciar o projeto faça a inserção dos grupos, na pasta ``exemplos`` encontrada no diretório raiz contém o arquivo de importação para o postman para maior facilidade de testes e o modelo da estrutura do servidor.
