# Cloudflare DNS Update Service
## About this software
Cloudflare DNS Update Service (software / service) is a simple java based / spring boot application to access the [Version 4 API of Cloudflare](https://api.cloudflare.com/).

If you have simple or old clients, that cannot communicate with Cloudflare by itself, this service translates your requests towards cloudflare.

## Usage
### Service installation / configuration
Download the latest release here from [GitHub](https://github.com/Ayokas/cloudflare-dnsupdate-service/releases) or build it on your own with maven.
Before you run it the first time, make sure you filled in all necessary Cloudflare properties in the application.properties file. You find an [example in the repository](../blob/master/src/main/resources/application.properties).
Fill in the following information:
* `cloudflare.record=` The Cloudflare record ID of your DNS-Record you want to update (currently only A / IPv4 records)
* `cloudlfare.zone=` Your Cloudflare zone (domain) where your records are in.
* `cloudflare.apibaseurl=https://api.cloudflare.com/client/v4` The current API endpoint address. As of version 1 of this service it is V4.

Note: This service uses spring boot. Therefore you can configure ports etc. in the application.properties file based on the [spring boot documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html).

You can now startup your application, the default port and address are `http://<yourMachineIP>:8080`.

### Configure your update client
The update request is based upon the Fritz!Box request from AVM. So your update URL have to look like this: `http://yourMachine:8080/rest/update/a?address=<ipaddr>&domain=<domain>`. If you are using a different client, replace the variables in brackets with the corresponding one of your client.

To authenticate yourself at Cloudflare, you need to send your registered e-mail address and Cloudflare API-Key as Basic-Authentication. Use your e-mail as user and your api-key as password.
[Read here how you get your Cloudflare API-Key](https://api.cloudflare.com/).

If you want to use an Cloudflare API-Token set as User `cloudlfare-token` and as password the API-Token. For API-Tokens check [Cloudflare User Profile 'API-Tokens' page](https://dash.cloudflare.com/profile/api-tokens).
## Issues and requests
Feel free to [submit issues and requests](https://github.com/Ayokas/cloudflare-dnsupdate-service/issues). However, keep in mind that I develop this in my spare time.