export interface Page<T> {
  pageInfo: PageInfo;
  edges: Edge<T>[];
}

export interface PageInfo {
  hasNextPage: boolean;
  endCursor: string | null;
}

export interface Edge<T> {
  cursor: string;
  node: T;
}
