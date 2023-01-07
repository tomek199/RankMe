import { NgModule } from '@angular/core';
import { Apollo, APOLLO_OPTIONS } from 'apollo-angular';
import { ApolloClientOptions, InMemoryCache, split } from '@apollo/client/core';
import { HttpLink } from 'apollo-angular/http';
import { environment } from '../environments/environment';
import { getMainDefinition } from '@apollo/client/utilities';
import { WebSocketLink } from '@apollo/client/link/ws';

export function createApollo(httpLink: HttpLink): ApolloClientOptions<any> {
  const http = httpLink.create({uri: environment.api.http});
  const ws = new WebSocketLink({
    uri: environment.api.ws
  });

  const link = split(
    // split based on operation type
    ({query}) => {
      const definition = getMainDefinition(query);
      return (
        definition.kind === 'OperationDefinition' && definition.operation === 'subscription'
      );
    },
    ws,
    http,
  );

  return {
    link: link,
    cache: new InMemoryCache(),
    defaultOptions: {
      query: {
        fetchPolicy: 'no-cache'
      },
      mutate: {
        fetchPolicy: 'no-cache'
      }
    }
  };
}

@NgModule({
  providers: [
    Apollo,
    {
      provide: APOLLO_OPTIONS,
      useFactory: createApollo,
      deps: [HttpLink],
    },
  ],
})
export class GraphQLModule {}
